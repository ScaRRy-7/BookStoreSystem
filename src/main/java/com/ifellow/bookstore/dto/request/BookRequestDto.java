package com.ifellow.bookstore.dto.request;

public record BookRequestDto(
        String title,
        String author,
        String genre,
        double retailPrice,
        double tradePrice) {}