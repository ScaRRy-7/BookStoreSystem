package com.ifellow.bookstore.dto.response;

import com.ifellow.bookstore.enumeration.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDto(
        Long id,
        LocalDateTime orderDateTime,
        OrderStatus orderStatus,
        Long warehouseId,
        BigDecimal totalPrice
) {
}
