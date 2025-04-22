package com.ifellow.bookstore.dto.request;

public record TransferRequestDto(
        Long sourceId,
        Long destinationId,
        BookBulkDto bookBulkDto
) {
}
