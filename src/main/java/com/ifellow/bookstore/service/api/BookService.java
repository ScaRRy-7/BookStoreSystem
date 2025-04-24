package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookFilter;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.exception.BookNotFoundException;
import com.ifellow.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponseDto save(BookRequestDto bookRequestDto);
    BookResponseDto findById(Long id);
    Object findAll(BookFilter filter, Pageable pageable);
    void checkBookExistence(Long id);
    Book findBookById(Long id);

}
