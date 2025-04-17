package com.ifellow.bookstore;

import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.api.AuthorRepository;
import com.ifellow.bookstore.repository.api.BookRepository;
import com.ifellow.bookstore.repository.api.GenreRepository;
import com.ifellow.bookstore.service.api.AuthorService;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.GenreService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        AuthorService authorService = context.getBean(AuthorService.class);
        GenreService genreService = context.getBean(GenreService.class);
        BookService bookService = context.getBean(BookService.class);

        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");
        GenreRequestDto genreRequestDto2 = new GenreRequestDto("Повесть");
        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);
        GenreResponseDto genreResponseDto2 = genreService.save(genreRequestDto2);

        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        AuthorResponseDto authorResponseDto = authorService.save(authorRequestDto);

        BookRequestDto bookRequestDto = new BookRequestDto("Преступление и наказание",
                authorResponseDto.id(), genreResponseDto.id(), new BigDecimal("128.00"));
        BookResponseDto bookResponseDto = bookService.save(bookRequestDto);

        BookRequestDto bookRequestDto2 = new BookRequestDto("ЕЩе одна книга",
                authorResponseDto.id(), genreResponseDto2.id(), new BigDecimal("256.00"));
        bookService.save(bookRequestDto2);

        Page<BookResponseDto> bookResponseDtoPage = bookService.findAllGroupedByGenre(PageRequest.of(0, 1));

        bookResponseDtoPage.getContent().forEach(System.out::println);



    }
}
