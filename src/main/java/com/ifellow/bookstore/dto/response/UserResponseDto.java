package com.ifellow.bookstore.dto.response;

import com.ifellow.bookstore.enumeration.RoleName;

import java.util.List;

public record UserResponseDto(
        String username,
        List<RoleName> roles
) {
}
