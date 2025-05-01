package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransferRequestDto(

        @Min(value = 1, message = "Book source id must not be less than 1")
        Long sourceId,

        @Min(value = 1, message = "Book destionation id must not be less than 1")
        Long destinationId,

        @NotNull
        BookBulkDto bookBulkDto
) {
}
