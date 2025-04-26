package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransferRequestDto(

        @Min(value = 1, message = "id источника книг не должен быть меньше 1")
        Long sourceId,

        @Min(value = 1, message = "id таргета книг не должен быть меньше 1")
        Long destinationId,

        @NotNull
        BookBulkDto bookBulkDto
) {
}
