package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record BookBulkDto(

        @Min(value = 1, message = "bookId должен быть не менее 1")
        Long bookId,

        @Min(value = 1, message = "Количество должно быть не менее 1")
        @Max(value = 1_000_000, message = "Количество должно быть не более 1.000.000")
        int quantity
) {

}
