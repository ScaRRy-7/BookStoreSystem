package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BookRequestDto (

    @NotBlank(message = "Book title cannot be null or empty")
    @Size(min = 1, max = 50, message = "The book title must contain from 1 to 50 characters")
    String title,

    @Min(value = 1, message = "authorId must be at least 1")
    Long authorId,

    @Min(value = 1, message = "genreId must be at least 1")
    Long genreId,

    @Min(value = 1, message = "The price must be at least 1")
    BigDecimal price
) {
}
