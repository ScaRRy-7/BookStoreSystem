package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreRequestDto(

        @NotBlank(message = "Название жанра не должно быть null или пустым")
        @Size(min = 1, max = 50, message = "Название жанра должно содержать от 1 до 50 символов")
        String name
) {
}
