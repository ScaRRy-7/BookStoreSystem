package com.ifellow.bookstore.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private final SecretKey jwtAccessSecret;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.expiration.access.minutes}")
    private int accessExpirationMinutes;

    public JwtUtils(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            UserDetailsService userDetailsService
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.userDetailsService = userDetailsService;
    }

    public String generateAccessTokenFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateAccessTokenFromUserDetails(userDetails);
    }

    public String generateAccessTokenFromUsername(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return generateAccessTokenFromUserDetails(userDetails);
    }

    private String generateAccessTokenFromUserDetails(UserDetails userDetails) {
        LocalDateTime now = LocalDateTime.now();
        Instant accessExpirationInstant = now.plusMinutes(accessExpirationMinutes).atZone(ZoneId.systemDefault()).toInstant();
        Date accessExpiration = Date.from(accessExpirationInstant);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .expiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parser()
                    .verifyWith(jwtAccessSecret)
                    .build().parseSignedClaims(accessToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getAccessClaims(@NonNull String accessToken) {
        return Jwts.parser()
                .verifyWith(jwtAccessSecret)
                .build().parseSignedClaims(accessToken).getPayload();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getAccessClaims(token);
        return claims.getSubject();
    }

    public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = getAccessClaims(token);
        List<String> roles = claims.get("roles", List.class);

        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }

}
