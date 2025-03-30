package com.ifellow.bookstore.dto.response;

import java.util.UUID;

public record StoreResponseDto(
        UUID id,
        String name,
        String address
) {}
