package com.ifellow.bookstore.dao.implementations;

import com.ifellow.bookstore.dao.interfaces.StoreInventoryDao;
import com.ifellow.bookstore.datasource.DataSource;
import com.ifellow.bookstore.model.Book;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StoreInventoryDaoImpl implements StoreInventoryDao {

    private final DataSource dataSource;
    private final UUID WAREHOUSE_ID = null;

    @Override
    public List<Book> findBooksByType(UUID storeId, Book bookType) {
        return dataSource.getBookReserve().stream()
                .filter(book -> storeId.equals(storeId))
                .filter(book -> book.equals(bookType))
                .collect(Collectors.toList());
    }

    @Override
    public void removeBooks(UUID storeId, List<Book> books) {
        books.stream().forEach(book -> dataSource.getBookReserve().remove(book));
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        return dataSource.getBookReserve().stream()
                .filter(book -> book.getAuthor().contains(author))
                .filter(book -> book.getStoreId() != WAREHOUSE_ID)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        return dataSource.getBookReserve().stream()
                .filter(book -> book.getTitle().contains(title))
                .filter(book -> book.getStoreId() != WAREHOUSE_ID)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Book>> groupBooksByGenre() {
        return dataSource.getBookReserve().stream()
                .filter(book -> book.getStoreId() != WAREHOUSE_ID)
                .collect(Collectors.groupingBy(Book::getGenre));
    }
}
