package com.ifellow.bookstore.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookFilter {
    private Long authorId;
    private Long genreId;
    private String title;
    private String authorFullName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean groupByGenre;
}
