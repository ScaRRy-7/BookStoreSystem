package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.OrderDao;
import com.ifellow.bookstore.dao.interfaces.StoreDao;
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
import com.ifellow.bookstore.model.Store;
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
    private StoreDao storeDao;
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
    private Store store;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        storeId = UUID.randomUUID();
        store = new Store(storeId, "Главный магазин", "Ул. Арбат");
        book = new Book("Мастер и Маргарита", "Михаил Булгаков", "Роман", 500, 200, storeId);
        order = new Order(storeId, List.of(book), 500);
        orderRequestDto = new OrderRequestDto(storeId, List.of(
                new BookRequestDto(book.getTitle(), book.getAuthor(), book.getGenre(), book.getRetailPrice(), book.getTradePrice())));


    }

    @Test
    @DisplayName("OrderServiceImpl получает валидные данные и создает заказ")
    void createOrder_ValidArgument_SavesOrderAndReturnsDto() {
        Mockito.when(storeInventoryDao.findBooksByType(storeId, book)).thenReturn(List.of(book, book));
        Mockito.when(storeDao.findById(storeId)).thenReturn(Optional.of(store));

        OrderResponseDto responseDto = orderService.createOrder(orderRequestDto);

        Assertions.assertNotNull(responseDto);
        Mockito.verify(orderDao, Mockito.times(1)).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("OrderServiceImpl выбрасывает исключение при передаче несуществующего id магазина")
    void createOrder_StoreNotFound_ThrowsException() {
        Mockito.when(storeDao.findById(storeId)).thenReturn(Optional.empty());

        assertThrows(StoreNotFoundException.class, () -> orderService.createOrder(orderRequestDto));
        Mockito.verify(orderDao, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("OrderServiceImpl выбрасывает исключение при недостаточном кол-ве книг")
    void createOrder_NotEnoughStock_ThrowsException() {
        Mockito.when(storeDao.findById(storeId)).thenReturn(Optional.of(store));
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

    @Test
    @DisplayName("OrderServiceImpl находит Order по существующему OrderId и возвращает responseDto")
    public void getOrder_ExistingOrderId_ReturnsResponseDto() {
        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponseDto responseDto = orderService.getOrder(orderId);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(responseDto.id(), order.getId());
    }

    @Test
    @DisplayName("OrderServiceImpl выбрасывает исключение при запросе несуществующего заказа")
    public void getOrder_OrderNotFound_ThrowsException() {
        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId));
    }

    @Test
    @DisplayName("OrderServiceImpl выполняет заказ со статусом CREATED")
    public void completeOrder_StatusCreated_UpdatesStatusAndRemovesBooks() {
        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(order));
        order.setStatus(OrderStatus.CREATED);

        orderService.completeOrder(orderId);

        Mockito.verify(storeInventoryDao, Mockito.times(1)).removeBooks(storeId, order.getBooks());
        Mockito.verify(orderDao, Mockito.times(1)).updateStatus(orderId, OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("OrderServiceImpl выбрасывает исключение при попытке выполнить заказ с неправильным статусом")
    public void completeOrder_InvalidStatus_ThrowsException() {
        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(order));
        order.setStatus(OrderStatus.CANCELED);

        Assertions.assertThrows(ChangeOrderStatusException.class, () -> orderService.completeOrder(orderId));
        Mockito.verify(storeInventoryDao, Mockito.never()).removeBooks(Mockito.any(), Mockito.any());
        Mockito.verify(orderDao, Mockito.never()).updateStatus(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("OrderServiceImpl выбрасывает исключение при попытке найти несуществующий Order по id")
    public void completeOder_OrderNotFound_ThrowsException() {
        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.completeOrder(orderId));
        Mockito.verify(storeInventoryDao, Mockito.never()).removeBooks(Mockito.any(), Mockito.any());
        Mockito.verify(orderDao, Mockito.never()).updateStatus(Mockito.any(), Mockito.any());
    }
}