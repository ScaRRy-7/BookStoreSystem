package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.ReceiptDao;
import com.ifellow.bookstore.dao.interfaces.SaleDao;
import com.ifellow.bookstore.dao.interfaces.StoreDao;
import com.ifellow.bookstore.dao.interfaces.StoreInventoryDao;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.ReceiptResponseDto;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreNotFoundException;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.mapper.ReceiptMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Receipt;
import com.ifellow.bookstore.model.Sale;
import com.ifellow.bookstore.service.interfaces.RetailSaleService;
import com.ifellow.bookstore.service.interfaces.StoreService;
import com.ifellow.bookstore.service.interfaces.WarehouseInventoryService;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RetailSaleServiceImpl implements RetailSaleService {

    private final StoreInventoryDao storeInventoryDao;
    private final StoreDao storeDao;
    private final SaleDao saleDao;
    private final ReceiptDao receiptDao;

    public ReceiptResponseDto processSale(UUID storeId, List<BookRequestDto> books)
            throws StoreNotFoundException, NotEnoughStockException {
        checkStoreExists(storeId);

        Map<Book, Long> requestedBooks = books.stream()
                .map(BookMapper::toModel)
                .peek(book -> book.setStoreId(storeId))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Book, List<Book>> availableBooks = checkAndGetAvailableBooks(storeId, requestedBooks);
        List<Book> booksToRemove = prepareBooksToRemove(availableBooks, requestedBooks);

        storeInventoryDao.removeBooks(storeId, booksToRemove);

        Sale sale = createSale(storeId, booksToRemove);
        saleDao.add(sale);

        Receipt receipt = createReceipt(sale.getId(), booksToRemove);
        receiptDao.add(receipt);

        return ReceiptMapper.toReceiptResponseDto(receipt);
    }

    private double calculateTotal(List<Book> books) {
        return books.stream().mapToDouble(Book::getRetailPrice).sum();
    }

    private void checkStoreExists(UUID storeId) throws StoreNotFoundException {
        storeDao.findById(storeId).orElseThrow(() -> new StoreNotFoundException("Store not found with id: " + storeId));
    }

    private Map<Book, List<Book>> checkAndGetAvailableBooks(UUID storeId, Map<Book, Long> requestedBooks)
            throws NotEnoughStockException {

        Map<Book, List<Book>> availableBooks = new HashMap<>();

        for (Map.Entry<Book, Long> entry : requestedBooks.entrySet()) {
            Book bookType = entry.getKey();
            long requiredCount = entry.getValue();

            List<Book> booksInStore = storeInventoryDao.findBooksByType(storeId, bookType);
            if (booksInStore.size() < requiredCount)
                throw new NotEnoughStockException("Not enough stock in store with id: " + storeId);

            availableBooks.put(bookType, booksInStore);
        }
        return availableBooks;
    }

    private List<Book> prepareBooksToRemove(Map<Book, List<Book>> availableBooks, Map<Book, Long> requestedBooks) {
        return requestedBooks.entrySet().stream()
                .flatMap(entry -> {
                    Book bookType = entry.getKey();
                    Long requiredCount = entry.getValue();
                    return availableBooks.get(bookType).stream().limit(requiredCount);
                }).collect(Collectors.toList());
    }

    private Sale createSale(UUID storeId, List<Book> books) {
        return Sale.builder()
                .id(UUID.randomUUID())
                .storeId(storeId)
                .books(books)
                .saleDate(new Date())
                .build();

    }

    private Receipt createReceipt(UUID saleId, List<Book> books) {
        return Receipt.builder()
                .saleId(saleId)
                .issueDate(new Date())
                .totalAmount(calculateTotal(books))
                .build();
    }
}
