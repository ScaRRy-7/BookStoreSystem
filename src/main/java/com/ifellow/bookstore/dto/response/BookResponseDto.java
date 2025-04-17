package com.ifellow.bookstore.dto.response;

import java.math.BigDecimal;

public record BookResponseDto (
    Long id,
    String title,
    String authorFullName,
    String genre,
    BigDecimal price
) {
}
