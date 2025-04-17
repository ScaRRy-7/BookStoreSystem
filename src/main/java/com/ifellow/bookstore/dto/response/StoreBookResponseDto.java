package com.ifellow.bookstore.dto.response;

import java.math.BigDecimal;

public record StoreBookResponseDto (
        Long id,
        String title,
        String authorFullName,
        String Genre,
        BigDecimal price,
        int quantity
) {
}
