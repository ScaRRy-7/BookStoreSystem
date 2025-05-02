package unit.service;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.service.api.StoreService;
import com.ifellow.bookstore.service.api.WarehouseService;
import com.ifellow.bookstore.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private StoreService storeService;

    @Mock
    private WarehouseService warehouseService;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Long warehouseId;
    private Long storeIdFrom;
    private Long storeIdTo;
    private Long bookId;
    private int quantity;

    @BeforeEach
    void setUp() {
        warehouseId = 1L;
        storeIdFrom = 2L;
        storeIdTo = 3L;
        bookId = 4L;
        quantity = 5;
    }

    @Test
    @DisplayName("Успешный перенос книги со склада в магазин")
    void transferBookFromWarehouseToStore_ValidData_TransferBook() {
        transferService.transferBookFromWarehouseToStore(warehouseId, storeIdTo, new BookBulkDto(bookId, quantity));

        Mockito.verify(warehouseService, Mockito.times(1)).removeBookFromWarehouse(warehouseId, new BookBulkDto(bookId, quantity));
        Mockito.verify(storeService, Mockito.times(1)).addBookToStore(storeIdTo, new BookBulkDto(bookId, quantity));
    }

    @Test
    @DisplayName("Успешный перенос книги из одного магазина в другой")
    void transferBookFromStoreToStore_ValidData_TransferBook() {
        transferService.transferBookFromStoreToStore(storeIdFrom, storeIdTo, new BookBulkDto(bookId, quantity));

        Mockito.verify(storeService, Mockito.times(1)).removeBookFromStore(storeIdFrom, new BookBulkDto(bookId, quantity));
        Mockito.verify(storeService, Mockito.times(1)).addBookToStore(storeIdTo, new BookBulkDto(bookId, quantity));
    }

    @Test
    @DisplayName("Исключение при переносе книги со склада из-за недостатка книг")
    void transferBookFromWarehouseToStore_NotEnoughStock_ThrowsException() {
        Mockito.doThrow(new NotEnoughStockException("Not enough stock"))
                .when(warehouseService).removeBookFromWarehouse(warehouseId, new BookBulkDto(bookId, quantity));

        assertThrows(NotEnoughStockException.class, () ->
                transferService.transferBookFromWarehouseToStore(warehouseId, storeIdTo, new BookBulkDto(bookId, quantity)));
        Mockito.verify(warehouseService, Mockito.times(1)).removeBookFromWarehouse(warehouseId, new BookBulkDto(bookId, quantity));
        Mockito.verify(storeService, Mockito.never()).addBookToStore(Mockito.anyLong(), Mockito.any());
    }

    @Test
    @DisplayName("Исключение при переносе книги из магазина из-за недостатка книг")
    void transferBookFromStoreToStore_NotEnoughStock_ThrowsException() {
        Mockito.doThrow(new NotEnoughStockException("Not enough stock"))
                .when(storeService).removeBookFromStore(storeIdFrom, new BookBulkDto(bookId, quantity));

        assertThrows(NotEnoughStockException.class, () ->
                transferService.transferBookFromStoreToStore(storeIdFrom, storeIdTo, new BookBulkDto(bookId, quantity)));
        Mockito.verify(storeService, Mockito.times(1)).removeBookFromStore(storeIdFrom, new BookBulkDto(bookId, quantity));
        Mockito.verify(storeService, Mockito.never()).addBookToStore(Mockito.anyLong(), Mockito.any());
    }
}