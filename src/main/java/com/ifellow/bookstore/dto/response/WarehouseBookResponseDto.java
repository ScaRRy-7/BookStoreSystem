package com.ifellow.bookstore.dto.response;

import java.math.BigDecimal;

public record WarehouseBookResponseDto(
        Long id,
        String title,
        String authorFullName,
        String Genre,
        BigDecimal price,
        int quantity
) {
}
