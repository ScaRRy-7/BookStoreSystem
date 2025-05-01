package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreRequestDto(

        @NotBlank(message = "Genre name must not be null or empty")
        @Size(min = 1, max = 50, message = "The genre name must contain from 1 to 50 characters.")
        String name
) {
}
