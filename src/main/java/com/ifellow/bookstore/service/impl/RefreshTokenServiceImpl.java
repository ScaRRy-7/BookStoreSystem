package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.exception.RefreshTokenException;
import com.ifellow.bookstore.model.RefreshToken;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.repository.RefreshTokenRepository;
import com.ifellow.bookstore.service.api.RefreshTokenService;
import com.ifellow.bookstore.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Value("${jwt.expiration.refresh.days}")
    private int expirationDays;

    @Override
    @Transactional
    public RefreshToken generateRefreshToken(String username) {
        User user = userService.findUserByUsername(username);

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevoked(false);

        Instant expirationDate = LocalDateTime.now().plusDays(expirationDays).atZone(ZoneId.systemDefault()).toInstant();
        refreshToken.setRefreshExpiration(expirationDate);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken findByToken(String token) throws RefreshTokenException {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found with UUID: " + token));
    }

    @Override
    @Transactional
    public RefreshToken verifyTokenAndRotate(String token) throws RefreshTokenException {
        RefreshToken refreshToken = findByToken(token);

        if (refreshToken.getRefreshExpiration().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException("Refresh token  with UUID: " + token + " was expired");
        }

        if (refreshToken.isRevoked()) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException("Refresh token with UUID: " + token + " is revoked.");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.delete(refreshToken);

        return generateRefreshToken(refreshToken.getUser().getUsername());
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String token) throws RefreshTokenException {
        RefreshToken refreshToken = findByToken(token);
        refreshTokenRepository.delete(refreshToken);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    public void deleteExpiredAndRevokedTokens() {
        refreshTokenRepository.deleteExpiredAndRevokedTokens(Instant.now());
    }
}
