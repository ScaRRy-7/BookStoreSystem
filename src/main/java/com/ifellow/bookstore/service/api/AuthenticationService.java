package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.JwtRequest;
import com.ifellow.bookstore.dto.request.RefreshTokenDto;
import com.ifellow.bookstore.dto.request.RegistrationUserDto;
import com.ifellow.bookstore.dto.response.JwtResponse;
import com.ifellow.bookstore.model.User;

public interface AuthenticationService {
    JwtResponse login(JwtRequest request);
    JwtResponse refresh(RefreshTokenDto oldRefreshToken);
    String logout(RefreshTokenDto refreshToken);
    String register(RegistrationUserDto registrationUserDto);
    User getUserInCurrentContext();
}
