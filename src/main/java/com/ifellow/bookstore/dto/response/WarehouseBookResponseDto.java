package com.ifellow.bookstore.dto.response;

public record WarehouseBookResponseDto(
        Long id,
        Long warehouseId,
        Long bookId,
        int quantity
) {
}
