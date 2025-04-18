package com.ifellow.bookstore.dto.response;

public record StoreBookResponseDto (
        Long id,
        Long storeId,
        Long bookId,
        int quantity
) {
}
