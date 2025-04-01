package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreNotFoundException;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.service.interfaces.TransferService;
import com.ifellow.bookstore.service.interfaces.StoreService;
import com.ifellow.bookstore.service.interfaces.WarehouseInventoryService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransferServiceImpl implements TransferService {

    private final WarehouseInventoryService warehouseInventoryService;
    private final StoreService storeService;
    private final Long EMPTY_STOCK = 0L;

    public TransferServiceImpl(WarehouseInventoryService warehouseInventoryService, StoreService storeService) {
        this.warehouseInventoryService = warehouseInventoryService;
        this.storeService = storeService;
    }

    @Override
    public void transferToStore(UUID storeId, List<BookRequestDto> bookRequestDtos)
            throws StoreNotFoundException, NotEnoughStockException {
        Map<Book, Long> requestedBooks = bookRequestDtos.stream()
                .map(BookMapper::toModel)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Book, Long> warehouseStock = warehouseInventoryService.getStockReport()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> BookMapper.toModel(entry.getKey()),
                        Map.Entry::getValue
                ));

        checkIsEnoughStock(requestedBooks, warehouseStock);

        requestedBooks.forEach((bookType, count) -> {
            warehouseInventoryService.findBooks(bookType)
                    .stream().limit(count).peek(book -> book.setStoreId(storeId))
                    .collect(Collectors.toList());
        });
    }

    private void checkIsEnoughStock(Map<Book, Long> requestedBooks, Map<Book, Long> warehouseStock) {
        requestedBooks.forEach((book, requiredCount) -> {
            long available = warehouseStock.getOrDefault(book, EMPTY_STOCK);
            if (available < requiredCount) throw new NotEnoughStockException("Not enough stock for book: " + book);
        });
    }
}
