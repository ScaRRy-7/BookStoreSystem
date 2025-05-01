package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequestDto(

        @NotBlank(message = "Author fullName can't be null or empty")
        @Size(min = 3, max = 50, message = "Author fullName must contains between 3 and 50 characters")
        String fullName
) {
}
