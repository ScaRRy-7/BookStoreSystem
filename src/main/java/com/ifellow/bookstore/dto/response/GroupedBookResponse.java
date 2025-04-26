package com.ifellow.bookstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupedBookResponse {
    private Map<String, List<BookResponseDto>> booksByGenre;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
