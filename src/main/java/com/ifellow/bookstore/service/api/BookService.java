package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponseDto save(BookRequestDto bookRequestDto);
    BookResponseDto findById(Long id);
    Page<BookResponseDto> findByGenreId(Long genreId, Pageable pageable);
    Page<BookResponseDto> findByAuthorId(Long authorId, Pageable pageable);
    Page<BookResponseDto> findByTitle(String title, Pageable pageable);
    Page<BookResponseDto> findByAuthorFullName(String fullName, Pageable pageable);
    Page<BookResponseDto> findByAuthorFullNameAndTitle(String fullName, String title, Pageable pageable);
    Page<BookResponseDto> findAllGroupedByGenre(Pageable pageable);
    Book findBookById(Long id);

}
