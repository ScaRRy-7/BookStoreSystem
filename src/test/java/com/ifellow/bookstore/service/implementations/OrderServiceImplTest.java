package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.OrderDao;
import com.ifellow.bookstore.dao.interfaces.StoreInventoryDao;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.request.OrderRequestDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.exception.ChangeOrderStatusException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.OrderNotFoundException;
import com.ifellow.bookstore.exception.StoreNotFoundException;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Order;
import com.ifellow.bookstore.service.interfaces.StoreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private StoreService storeService;
    @Mock
    private OrderDao orderDao;
    @Mock
    private StoreInventoryDao storeInventoryDao;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID orderId;
    private UUID storeId;
    private Book book;
    private Order order;
    private OrderRequestDto orderRequestDto;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        storeId = UUID.randomUUID();
        book = new Book("Мастер и Маргарита", "Михаил Булгаков", "Роман", 500, 200, storeId);
        order = new Order(storeId, List.of(book), 500);
        orderRequestDto = new OrderRequestDto(storeId, List.of(
                new BookRequestDto(book.getTitle(), book.getAuthor(), book.getGenre(), book.getRetailPrice(), book.getTradePrice())));


    }

    @Test
    @DisplayName("OrderServiceImpl получает валидные данные и создает заказ")
    void createOrder_ValidArgument_SavesOrderAndReturnsDto() {
        Mockito.when(storeInventoryDao.findBooksByType(storeId, book)).thenReturn(List.of(book, book));

        OrderResponseDto responseDto = orderService.createOrder(orderRequestDto);

        Assertions.assertNotNull(responseDto);
        Mockito.verify(orderDao, Mockito.times(1)).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("OrderServiceImpl выбрасывает исключение при передаче несуществующего id магазина")
    void createOrder_StoreNotFound_ThrowsException() {
        Mockito.when(storeService.findById(Mockito.any(UUID.class))).thenThrow(new StoreNotFoundException("Store not found"));

        assertThrows(StoreNotFoundException.class, () -> orderService.createOrder(orderRequestDto));
        Mockito.verify(orderDao, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("OrderServiceImpl выбрасывает исключение при недостаточном кол-ве книг")
    void createOrder_NotEnoughStock_ThrowsException() {
        Mockito.when(storeInventoryDao.findBooksByType(storeId, book)).thenReturn(List.of());

        assertThrows(NotEnoughStockException.class, () -> orderService.createOrder(orderRequestDto));
        Mockito.verify(orderDao, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("OrdeServiceImpl отменяет заказ со стаусом CREATED ")
    void cancelOrder_CreatedStatus_UpdatesStatus() {
        Mockito.when(orderDao.getStatusByOrderId(orderId)).thenReturn(Optional.of(OrderStatus.CREATED));

        orderService.cancelOrder(orderId);

        Mockito.verify(orderDao, Mockito.times(1)).updateStatus(orderId, OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("OrdeServiceImpl выбрасывает исключение при попытке отмены заказа с неподходящим статусом")
    void cancelOrder_InvalidStatus_ThrowsException() {
        Mockito.when(orderDao.getStatusByOrderId(orderId)).thenReturn(Optional.of(OrderStatus.COMPLETED));

        Assertions.assertThrows(ChangeOrderStatusException.class, () -> orderService.cancelOrder(orderId));
        Mockito.verify(orderDao, Mockito.never()).updateStatus(orderId, OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("OrderServiceImpl выбрасывает исключение при попытке отмены несуществующего заказа")
    void cancelOrder_OrderNotFound_ThrowsException() {
        Mockito.when(orderDao.getStatusByOrderId(orderId)).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(orderId));
        Mockito.verify(orderDao, Mockito.never()).updateStatus(orderId, OrderStatus.CANCELED);
    }
}