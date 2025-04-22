package com.ifellow.bookstore.dto.request;

public record BookBulkDto(
        Long bookId,
        int quantity
) {

}
