package com.ifellow.bookstore.service.interfaces;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;

import java.util.List;
import java.util.Map;

public interface WarehouseInventoryService {

    void wholesaleBookDelivery(List<BookRequestDto> bookRequestDtos);
    Map<BookResponseDto, Long> getStockReport();
    void addBook(BookRequestDto bookRequestDto);
    void removeBook(BookRequestDto bookRequestDto);
    List<Book> findBooksByType(Book bookType);
    void removeBooks(List<Book> books);
    List<BookResponseDto> findBooksByAuthor(String author);
    List<BookResponseDto> findBooksByTitle(String title);
    Map<String, List<BookResponseDto>> groupBooksByGenre();
}
