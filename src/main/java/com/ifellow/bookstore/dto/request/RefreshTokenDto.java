package com.ifellow.bookstore.dto.request;


import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(
        @NotBlank(message = "Refresh token can not be null or empty")
        String refreshToken
) {
}
