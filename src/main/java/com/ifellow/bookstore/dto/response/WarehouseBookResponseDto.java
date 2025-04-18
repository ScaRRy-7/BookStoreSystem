package com.ifellow.bookstore.dto.response;

import java.math.BigDecimal;

public record WarehouseBookResponseDto(
        Long id,
        Long warehouseId,
        Long bookId,
        int quantity
) {
}
