package com.ifellow.bookstore.dao.interfaces;

import com.ifellow.bookstore.model.Book;

import java.util.List;
import java.util.UUID;

public interface StoreInventoryDao {
    List<Book> findBooksByType(UUID storeId, Book bookType);
    void removeBooks(UUID storeId, List<Book> books);
}
