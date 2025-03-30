package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;

import java.util.UUID;

public class BookMapper {

    public static Book toModel(BookRequestDto dto) {
        return new Book(
                dto.title(),
                dto.author(),
                dto.genre(),
                dto.retailPrice(),
                dto.tradePrice(),
                null
        );
    }

    public static BookResponseDto toResponseDTO(Book model) {
        return new BookResponseDto(
                model.getTitle(),
                model.getAuthor(),
                model.getGenre(),
                model.getRetailPrice(),
                model.getTradePrice(),
                model.getStoreId()
        );
    }

    public static Book toModel(BookResponseDto dto) {
        return new Book(
                dto.title(),
                dto.author(),
                dto.genre(),
                dto.retailPrice(),
                dto.tradePrice(),
                dto.storeId()
        );
    }
}
