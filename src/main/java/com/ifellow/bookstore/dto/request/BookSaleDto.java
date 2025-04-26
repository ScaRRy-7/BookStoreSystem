package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record BookSaleDto(

        @Min(value = 1, message = "bookId не должен быть меньше 1")
        Long bookId,

        @Min(value = 1, message = "Количество не должно быть меньше 1")
        @Max(value = 1_000_000, message = "Количество не должно быть больше 1.000.000")
        int quantity
) {
}
