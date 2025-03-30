package com.ifellow.bookstore.dao.interfaces;

import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;

import java.util.List;
import java.util.Map;

public interface WarehouseInventoryDao {


    void wholesaleDelivery(List<Book> books);
    void addBook(Book book);
    void removeBook(Book book);
    Map<Book, Long> getBooks();
    List<Book> findBooks(Book bookType);
    void removeBooks(List<Book> books);
    List<Book> findBooksByAuthor(String author);
    List<Book> findBooksByTitle(String title);
    Map<String, List<Book>> groupBooksByGenre();
}
