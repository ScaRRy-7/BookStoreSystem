package com.ifellow.bookstore.dao.implementations;

import com.ifellow.bookstore.dao.interfaces.WarehouseInventoryDao;
import com.ifellow.bookstore.datasource.DataSource;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.model.Book;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WarehouseInventoryDaoImpl implements WarehouseInventoryDao {

    private final DataSource dataSource;
    private final UUID WAREHOUSE_ID = null;

    @Override
    public void wholesaleDelivery(List<Book> books) {
        dataSource.getBookReserve().addAll(books);
    }

    @Override
    public Map<Book, Long> getBooks() {
        return dataSource.getBookReserve().stream()
                .filter(b -> b.getStoreId() == WAREHOUSE_ID)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
    }

    @Override
    public List<Book> findBooks(Book bookType) {
        return dataSource.getBookReserve().stream()
                .filter(book -> book.getStoreId() == WAREHOUSE_ID)
                .filter(book -> book.equals(bookType))
                .collect(Collectors.toList());
    }

    @Override
    public void removeBooks(List<Book> books) {
        dataSource.getBookReserve().removeAll(books);
    }

    @Override
    public void addBook(Book book) {
        dataSource.getBookReserve().add(book);

    }

    @Override
    public void removeBook(Book book) {
        dataSource.getBookReserve().remove(book);
    }

    @Override
    public List<Book> findBooksByAuthor(String author) {
        return dataSource.getBookReserve().stream()
                .filter(book -> book.getAuthor().contains(author))
                .filter(book -> book.getStoreId() == WAREHOUSE_ID)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        return dataSource.getBookReserve().stream()
                .filter(book -> book.getTitle().contains(title))
                .filter(book -> book.getStoreId() == WAREHOUSE_ID)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Book>> groupBooksByGenre() {
        return dataSource.getBookReserve().stream()
                .filter(book -> book.getStoreId() == WAREHOUSE_ID)
                .collect(Collectors.groupingBy(Book::getGenre));
    }
}
