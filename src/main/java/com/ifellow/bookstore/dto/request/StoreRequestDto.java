package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreRequestDto(

    @NotBlank(message = "Адрес магазина не должен быть null или пустым")
    @Size(min = 3, max = 80, message = "Длина адреса должна содержать от 3 до 80 символов")
    String address
) {
}
