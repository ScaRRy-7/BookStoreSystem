package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper implements ToEntityMapper<BookRequestDto, Book>, ToDtoMapper<Book, BookResponseDto> {

    public Book toEntity(BookRequestDto bookRequestDto) {
        return Book.builder()
                .title(bookRequestDto.title())
                .price(bookRequestDto.price())
                .build();
    }

    public BookResponseDto toDto(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getGenre().getId(),
                book.getPrice()
        );
    }
}
