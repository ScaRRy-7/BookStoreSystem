package unit.service;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.exception.OrderStatusException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.OrderException;
import com.ifellow.bookstore.mapper.OrderMapper;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.OrderRepository;
import com.ifellow.bookstore.service.api.AuthenticationService;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.WarehouseService;
import com.ifellow.bookstore.service.impl.OrderServiceImpl;
import com.ifellow.bookstore.util.OrderUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private BookService bookService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderUtils orderUtils;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Long warehouseId;
    private Long bookId;
    private Long orderId;
    private int quantity;
    private BookOrderDto bookOrderDto;
    private Warehouse warehouse;
    private Book book;
    private Order order;
    private OrderItem orderItem;
    private OrderResponseDto orderResponseDto;
    private LocalDateTime orderTime;

    @BeforeEach
    void setUp() {
        warehouseId = 1L;
        bookId = 1L;
        orderId = 1L;
        quantity = 2;
        orderTime = LocalDateTime.now();
        bookOrderDto = new BookOrderDto(bookId, quantity);

        warehouse = new Warehouse();
        warehouse.setId(warehouseId);

        book = new Book();
        book.setId(bookId);
        book.setPrice(BigDecimal.valueOf(100));

        order = new Order();
        order.setId(orderId);
        order.setWarehouse(warehouse);
        order.setOrderDateTime(orderTime);
        order.setOrderStatus(OrderStatus.CREATED);

        orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setBook(book);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(book.getPrice());

        order.setOrderItemList(List.of(orderItem));
        order.setTotalPrice(BigDecimal.valueOf(200));

        orderResponseDto = new OrderResponseDto(orderId, 1L, orderTime, OrderStatus.CREATED, warehouseId, BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("Успешное создание заказа")
    void create_ValidData_CreatesOrder() {
        Mockito.when(warehouseService.findWarehouseById(warehouseId)).thenReturn(warehouse);
        Mockito.when(bookService.findBookById(bookId)).thenReturn(book);
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
        Mockito.when(orderMapper.toDto(Mockito.any(Order.class))).thenReturn(orderResponseDto);
        Mockito.when(authenticationService.getUserInCurrentContext()).thenReturn(new User(1L, "username", "password", Set.of(), List.of(), List.of()));

        OrderResponseDto result = orderService.create(warehouseId, List.of(bookOrderDto));

        assertNotNull(result);
        assertEquals(orderResponseDto, result);
        Mockito.verify(warehouseService).removeBookFromWarehouse(warehouseId, new BookBulkDto(bookId, quantity));
        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Исключение при недостатке книг на складе")
    void create_NotEnoughStock_ThrowsException() {
        Mockito.when(warehouseService.findWarehouseById(warehouseId)).thenReturn(warehouse);
        Mockito.doThrow(new NotEnoughStockException("Not enough stock")).when(warehouseService).removeBookFromWarehouse(warehouseId, new BookBulkDto(bookId, quantity));

        assertThrows(NotEnoughStockException.class, () -> orderService.create(warehouseId, List.of(bookOrderDto)));
        Mockito.verify(warehouseService).removeBookFromWarehouse(warehouseId, new BookBulkDto(bookId, quantity));
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Успешное завершение заказа")
    void completeById_ValidOrderId_CompletesOrder() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderMapper.toDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.completeById(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        Mockito.verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Исключение при завершении несуществующего заказа")
    void completeById_NotExistingOrder_ThrowsException() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.completeById(orderId));
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Исключение при завершении заказа не в статусе CREATED")
    void completeById_NotCreatedStatus_ThrowsException() {
        order.setOrderStatus(OrderStatus.COMPLETED);
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(OrderStatusException.class, () -> orderService.completeById(orderId));
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Успешная отмена заказа")
    void cancelById_ValidOrderId_CancelsOrder() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderMapper.toDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.cancelById(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        Mockito.verify(warehouseService).addBookToWarehouse(warehouseId, new BookBulkDto(bookId, quantity));
        Mockito.verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Исключение при отмене несуществующего заказа")
    void cancelById_NotExistingOrder_ThrowsException() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.cancelById(orderId));
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Исключение при отмене заказа не в статусе CREATED")
    void cancelById_NotCreatedStatus_ThrowsException() {
        order.setOrderStatus(OrderStatus.COMPLETED);
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(OrderStatusException.class, () -> orderService.cancelById(orderId));
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Получение заказа по ID")
    void findById_ExistingOrderId_ReturnsOrder() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderMapper.toDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.findById(orderId);

        assertNotNull(result);
        assertEquals(orderResponseDto, result);
        Mockito.verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Исключение при поиске несуществующего заказа")
    void findById_NotExistingOrder_ThrowsException() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.findById(orderId));
    }
}