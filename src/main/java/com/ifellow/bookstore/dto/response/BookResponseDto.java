package com.ifellow.bookstore.dto.response;

import java.util.UUID;

public record BookResponseDto(
        String title,
        String author,
        String genre,
        double retailPrice,
        double tradePrice,
        UUID storeId) {}