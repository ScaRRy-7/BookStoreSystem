package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.ReceiptDao;
import com.ifellow.bookstore.dao.interfaces.SaleDao;
import com.ifellow.bookstore.dao.interfaces.StoreDao;
import com.ifellow.bookstore.dao.interfaces.StoreInventoryDao;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.ReceiptResponseDto;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreNotFoundException;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Receipt;
import com.ifellow.bookstore.model.Sale;
import com.ifellow.bookstore.model.Store;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RetailSaleServiceImplTest {

    @Mock
    private StoreInventoryDao storeInventoryDao;

    @Mock
    private StoreDao storeDao;

    @Mock
    private SaleDao saleDao;

    @Mock
    private ReceiptDao receiptDao;

    @InjectMocks
    private RetailSaleServiceImpl retailSaleService;

    private UUID storeId;
    private Book book;
    private Store store;
    private BookRequestDto bookRequestDto;
    private List<BookRequestDto> books;

    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID();
        store = new Store(storeId, "Главный магазин", "Ул. Арбат");
        book = new Book("Мастер и Маргарита", "Михаил Булгаков", "Роман", 500, 200, storeId);
        bookRequestDto = new BookRequestDto(
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getRetailPrice(),
                book.getTradePrice()
        );
        books = List.of(bookRequestDto);
    }

    @Test
    @DisplayName("RetailServiceImpl успешно производит розничную продажу с корректными аргументами")
    void processSale_ValidData_CreateSaleAndReceipt() {
        Mockito.when(storeDao.findById(storeId)).thenReturn(Optional.of(store));
        Mockito.when(storeInventoryDao.findBooksByType(storeId, book)).thenReturn(List.of(book, book));

        ReceiptResponseDto responseDto = retailSaleService.processSale(storeId, books);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(500, responseDto.totalAmount());
        Mockito.verify(storeInventoryDao, Mockito.times(1)).removeBooks(storeId, List.of(book));
        Mockito.verify(saleDao, Mockito.times(1)).add(Mockito.any(Sale.class));
        Mockito.verify(receiptDao, Mockito.times(1)).add(Mockito.any(Receipt.class));
    }

    @Test
    @DisplayName("RetailServiceImpl выбрасывает исключение при попытке найти несуществующий Store по id")
    public void processSale_StoreNotFound_ThrowsException() {
        Mockito.when(storeDao.findById(storeId)).thenReturn(Optional.empty());

        Assertions.assertThrows(StoreNotFoundException.class, () -> retailSaleService.processSale(storeId, books));
        Mockito.verify(storeDao, Mockito.times(1)).findById(storeId);
        Mockito.verify(storeInventoryDao, Mockito.never()).findBooksByType(Mockito.any(), Mockito.any());
        Mockito.verify(saleDao, Mockito.never()).add(Mockito.any(Sale.class));
        Mockito.verify(receiptDao, Mockito.never()).add(Mockito.any(Receipt.class));
    }

    @Test
    @DisplayName("RetailServiceImpl выбрасывает исключение при недостаточном кол-ве книг в магазине")
    public void processSale_NotEnoughStock_ThrowsException() {
        Mockito.when(storeDao.findById(storeId)).thenReturn(Optional.of(store));
        Mockito.when(storeInventoryDao.findBooksByType(storeId, book)).thenReturn(List.of());

        Assertions.assertThrows(NotEnoughStockException.class, () -> retailSaleService.processSale(storeId, books));
        Mockito.verify(storeDao, Mockito.times(1)).findById(storeId);
        Mockito.verify(storeInventoryDao, Mockito.times(1)).findBooksByType(Mockito.any(), Mockito.any());
        Mockito.verify(saleDao, Mockito.never()).add(Mockito.any(Sale.class));
        Mockito.verify(receiptDao, Mockito.never()).add(Mockito.any(Receipt.class));

    }
}