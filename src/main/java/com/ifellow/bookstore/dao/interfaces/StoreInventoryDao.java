package com.ifellow.bookstore.dao.interfaces;

import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StoreInventoryDao {
    List<Book> findBooksByType(UUID storeId, Book bookType);
    void removeBooks(UUID storeId, List<Book> books);
    List<Book> findBooksByAuthor(String author);
    List<Book> findBooksByTitle(String title);
    Map<String, List<Book>> groupBooksByGenre();
}
