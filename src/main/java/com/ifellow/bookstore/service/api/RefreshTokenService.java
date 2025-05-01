package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.exception.RefreshTokenException;
import com.ifellow.bookstore.model.RefreshToken;

public interface RefreshTokenService {

    RefreshToken generateRefreshToken(String username);
    RefreshToken findByToken(String token);
    RefreshToken verifyTokenAndRotate(String token) throws RefreshTokenException;
    void deleteRefreshToken(String token) throws RefreshTokenException;
    void deleteExpiredAndRevokedTokens();
}
