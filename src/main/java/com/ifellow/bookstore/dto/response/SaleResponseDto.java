package com.ifellow.bookstore.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleResponseDto(
        Long id,
        Long userId,
        LocalDateTime time,
        Long storeId,
        BigDecimal totalPrice
) {
}
