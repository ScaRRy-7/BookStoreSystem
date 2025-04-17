package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toEntity(BookRequestDto bookRequestDto) {
        return Book.builder()
                .title(bookRequestDto.title())
                .price(bookRequestDto.price())
                .build();
    }

    public BookResponseDto toResponseDto(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getFullName(),
                book.getGenre().getName(),
                book.getPrice()
        );
    }
}
