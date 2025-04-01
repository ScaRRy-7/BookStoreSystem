package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.service.interfaces.StoreService;
import com.ifellow.bookstore.service.interfaces.WarehouseInventoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private WarehouseInventoryService warehouseInventoryService;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    @DisplayName("TransferServiceImpl выбрасывает исключение по причине недостаточного кол-ва книг на складе")
    void transferToStore_ValidArgumentsAndNotEnoughStock_ThrowsException() {
        UUID storeId = UUID.randomUUID();
        BookResponseDto bookInWareHouse = new BookResponseDto(
                "Мастер и маргарита", "Булгаков", "Роман", 500, 200, storeId);
        BookRequestDto RequestedBook = new BookRequestDto(
                "Мастер и маргарита", "Булгаков", "Роман", 500, 200);
        List<BookRequestDto> requestedBooks = List.of(RequestedBook, RequestedBook);
        Mockito.when(warehouseInventoryService.getStockReport()).thenReturn(Map.of(bookInWareHouse, 1L));


        Assertions.assertThrows(NotEnoughStockException.class ,() -> transferService.transferToStore(storeId, requestedBooks));
    }

    @Test
    @DisplayName("TransferSerivceImpl получает корректные аргументы передает книги на склад")
    void transferToStore_ValidArgumentsAndEnoughStock_transfersBooks() {
        UUID wareHouseId = null;
        UUID storeId = UUID.randomUUID();
        BookResponseDto bookInWareHouse = new BookResponseDto(
                "Мастер и маргарита", "Булгаков", "Роман", 500, 200, wareHouseId);
        BookRequestDto RequestedBook = new BookRequestDto(
                "Мастер и маргарита", "Булгаков", "Роман", 500, 200);
        List<BookRequestDto> requestedBooks = List.of(RequestedBook, RequestedBook);
        Mockito.when(warehouseInventoryService.getStockReport()).thenReturn(Map.of(bookInWareHouse, 2L));


        Assertions.assertDoesNotThrow(() -> transferService.transferToStore(storeId, requestedBooks));
    }
}