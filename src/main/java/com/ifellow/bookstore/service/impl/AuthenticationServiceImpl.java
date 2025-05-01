package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.JwtRequest;
import com.ifellow.bookstore.dto.request.RefreshTokenDto;
import com.ifellow.bookstore.dto.request.RegistrationUserDto;
import com.ifellow.bookstore.dto.response.JwtResponse;
import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.exception.UserException;
import com.ifellow.bookstore.model.RefreshToken;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.service.api.AuthenticationService;
import com.ifellow.bookstore.service.api.RefreshTokenService;
import com.ifellow.bookstore.service.api.RoleService;
import com.ifellow.bookstore.service.api.UserService;
import com.ifellow.bookstore.util.JwtUtils;
import com.ifellow.bookstore.validator.PasswordMatchesValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final PasswordMatchesValidator passwordMatchesValidator;
    private final RoleService roleService;

    @Override
    @Transactional
    public JwtResponse login(@NonNull JwtRequest jwtRequest) throws AuthenticationException {
        Authentication successAuthentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));

        String accessToken = jwtUtils.generateAccessTokenFromAuthentication(successAuthentication);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(jwtRequest.getUsername());

        return new JwtResponse(accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public JwtResponse refresh(RefreshTokenDto oldRefreshToken) {
        RefreshToken newRefreshToken = refreshTokenService.verifyTokenAndRotate(oldRefreshToken.refreshToken());
        String newAccessToken = jwtUtils.generateAccessTokenFromUsername(newRefreshToken.getUser().getUsername());

        return new JwtResponse(newAccessToken, newRefreshToken.getToken());
    }

    @Override
    @Transactional
    public String logout(RefreshTokenDto refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken.refreshToken());
        return "Logout successful!";
    }

    @Override
    @Transactional
    public String register(RegistrationUserDto registrationUserDto) {
        if (!passwordMatchesValidator.isValid(registrationUserDto))
            throw new UserException("Password does not match!");

        User user = User.builder()
                .username(registrationUserDto.getUsername())
                .password(passwordEncoder.encode(registrationUserDto.getPassword()))
                .roles(
                        List.of(roleService.findByName(RoleName.ROLE_CLIENT)))
                .build();
        userService.saveUser(user);

        return "Register successful! Go to login page to get access + refresh tokens!";
    }
}
