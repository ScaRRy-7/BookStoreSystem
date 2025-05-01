package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JwtRequest {

    @NotBlank(message = "username cannot be null or empty")
    @Size(min = 4, max = 20, message = "username must be between 4 and 20 characters long")
    private String username;

    @NotBlank(message = "password cannot be null or empty")
    @Size(min = 8, message = "password must contain at least 8 characters")
    private String password;
}
