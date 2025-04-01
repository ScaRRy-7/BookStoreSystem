package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.WarehouseInventoryDao;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.service.interfaces.WarehouseInventoryService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WarehouseInventoryServiceImpl implements WarehouseInventoryService {

    private final WarehouseInventoryDao warehouseInventoryDAO;
    private final UUID WAREHOUSE_ID = null;

    @Override
    public void wholesaleBookDelivery(List<BookRequestDto> bookRequestDtos) {
        List<Book> books = bookRequestDtos.stream()
                .map(BookMapper::toModel)
                .collect(Collectors.toList());

        warehouseInventoryDAO.wholesaleDelivery(books);
    }

    @Override
    public Map<BookResponseDto, Long> getStockReport() {

        return warehouseInventoryDAO.getBooks().entrySet()
                .stream().collect(Collectors.toMap(
                        entry -> BookMapper.toResponseDTO(entry.getKey()),
                        Map.Entry::getValue
                ));
    }

    @Override
    public void addBook(BookRequestDto bookRequestDto)  {
        if (bookRequestDto == null) throw new IllegalArgumentException("Incorrect argument was passed");

        warehouseInventoryDAO.addBook(BookMapper.toModel(bookRequestDto));
    }

    @Override
    public void removeBook(BookRequestDto bookRequestDto)  {
        if (bookRequestDto == null) throw new IllegalArgumentException("Incorrect argument was passed");

        warehouseInventoryDAO.removeBook(BookMapper.toModel(bookRequestDto));
    }

    @Override
    public List<Book> findBooks(Book bookType) {
        return warehouseInventoryDAO.findBooks(bookType);
    }

    @Override
    public void removeBooks(List<Book> books) throws IllegalArgumentException {
        if (books == null) throw new IllegalArgumentException("Incorrect argument was passed");

        if (books.stream().anyMatch(book -> book.getStoreId() != WAREHOUSE_ID))
            throw new IllegalArgumentException("Book isn't in warehouse");

        warehouseInventoryDAO.removeBooks(books);
    }

    @Override
    public List<BookResponseDto> findBooksByAuthor(String author) {
        if (author == null) throw new IllegalArgumentException("Incorrect argument was passed");

        return warehouseInventoryDAO.findBooksByAuthor(author).stream()
                .map(BookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDto> findBooksByTitle(String title) {
        return warehouseInventoryDAO.findBooksByTitle(title).stream()
                .map(BookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<BookResponseDto>> groupBooksByGenre() {
        return warehouseInventoryDAO.groupBooksByGenre().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(BookMapper::toResponseDTO)
                                .collect(Collectors.toList())
                ));
    }
}
