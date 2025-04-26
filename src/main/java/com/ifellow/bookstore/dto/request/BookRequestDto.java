package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BookRequestDto (

    @NotBlank(message = "Название книги не может быть null или пустым")
    @Size(min = 1, max = 50, message = "Название книги должно содержать от 1 до 50 символов")
    String title,

    @Min(value = 1, message = "authorId должен быть не менее 1")
    Long authorId,

    @Min(value = 1, message = "genreId должен быть не менее 1")
    Long genreId,

    @Min(value = 1, message = "Цена должна быть не менее 1")
    BigDecimal price
) {
}
