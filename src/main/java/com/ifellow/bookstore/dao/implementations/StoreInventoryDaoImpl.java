package com.ifellow.bookstore.dao.implementations;

import com.ifellow.bookstore.dao.interfaces.StoreInventoryDao;
import com.ifellow.bookstore.datasource.DataSource;
import com.ifellow.bookstore.model.Book;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StoreInventoryDaoImpl implements StoreInventoryDao {

    private final DataSource dataSource;

    @Override
    public List<Book> findBooksByType(UUID storeId, Book bookType) {
        return dataSource.getBookReserve().stream()
                .filter(book -> storeId.equals(storeId))
                .filter(book -> book.equals(bookType))
                .collect(Collectors.toList());
    }

    @Override
    public void removeBooks(UUID storeId, List<Book> books) {
        dataSource.getBookReserve().removeAll(books);
    }
}
