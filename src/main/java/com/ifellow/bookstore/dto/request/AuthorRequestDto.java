package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthorRequestDto(

        @NotBlank(message = "ФИО автора не может быть null или пустым")
        @Size(min = 3, max = 50, message = "ФИО автора должно содержать от 3 до 50 символов")
        String fullName
) {
}
