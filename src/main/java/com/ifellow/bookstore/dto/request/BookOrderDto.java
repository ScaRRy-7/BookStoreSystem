package com.ifellow.bookstore.dto.request;

public record BookOrderDto(
        Long bookId,
        int quantity
) {
}
