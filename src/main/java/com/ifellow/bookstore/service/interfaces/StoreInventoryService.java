package com.ifellow.bookstore.service.interfaces;

import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;

import java.util.List;
import java.util.Map;

public interface StoreInventoryService {

    List<BookResponseDto> findBooksByAuthor(String author);
    List<BookResponseDto> findBooksByTitle(String title);
    Map<String, List<BookResponseDto>> groupBooksByGenre();
}
