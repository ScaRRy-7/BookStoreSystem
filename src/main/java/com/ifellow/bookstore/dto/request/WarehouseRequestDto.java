package com.ifellow.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseRequestDto(

        @NotBlank(message = "Warehouse address must not be null or empty")
        @Size(min = 3, max = 80, message = "The address length must be between 3 and 80 characters")
        String address
) {
}
