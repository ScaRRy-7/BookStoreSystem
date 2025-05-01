package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record BookSaleDto(

        @Min(value = 1, message = "bookId must not be less than 1")
        Long bookId,

        @Min(value = 1, message = "The quantity should not be less than 1")
        @Max(value = 1_000_000, message = "The quantity should not be more than 1,000,000")
        int quantity
) {
}
