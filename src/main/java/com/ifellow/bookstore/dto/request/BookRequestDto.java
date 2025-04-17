package com.ifellow.bookstore.dto.request;

import java.math.BigDecimal;

public record BookRequestDto (
    String title,
    Long authorId,
    Long genreId,
    BigDecimal price
) {
}
