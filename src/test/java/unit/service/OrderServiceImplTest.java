package unit.service;

import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.exception.ChangeOrderStatusException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.OrderNotFoundException;
import com.ifellow.bookstore.mapper.OrderMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Order;
import com.ifellow.bookstore.model.OrderItem;
import com.ifellow.bookstore.model.Warehouse;
import com.ifellow.bookstore.repository.api.OrderRepository;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.WarehouseService;
import com.ifellow.bookstore.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private OrderMapper orderMapper;

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

        orderResponseDto = new OrderResponseDto(orderId, orderTime, OrderStatus.CREATED, warehouseId, BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("Успешное создание заказа")
    void create_ValidData_CreatesOrder() {
        Mockito.when(warehouseService.findWarehouseById(warehouseId)).thenReturn(warehouse);
        Mockito.when(bookService.findBookById(bookId)).thenReturn(book);
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
        Mockito.when(orderMapper.toResponseDto(Mockito.any(Order.class))).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.create(warehouseId, List.of(bookOrderDto));

        assertNotNull(result);
        assertEquals(orderResponseDto, result);
        Mockito.verify(warehouseService).removeBookFromWarehouse(warehouseId, bookId, quantity);
        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Исключение при недостатке книг на складе")
    void create_NotEnoughStock_ThrowsException() {
        Mockito.when(warehouseService.findWarehouseById(warehouseId)).thenReturn(warehouse);
        Mockito.doThrow(new NotEnoughStockException("Not enough stock")).when(warehouseService).removeBookFromWarehouse(warehouseId, bookId, quantity);

        assertThrows(NotEnoughStockException.class, () -> orderService.create(warehouseId, List.of(bookOrderDto)));
        Mockito.verify(warehouseService).removeBookFromWarehouse(warehouseId, bookId, quantity);
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Успешное завершение заказа")
    void completeById_ValidOrderId_CompletesOrder() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.completeById(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        Mockito.verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Исключение при завершении несуществующего заказа")
    void completeById_NotExistingOrder_ThrowsException() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.completeById(orderId));
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Исключение при завершении заказа не в статусе CREATED")
    void completeById_NotCreatedStatus_ThrowsException() {
        order.setOrderStatus(OrderStatus.COMPLETED);
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(ChangeOrderStatusException.class, () -> orderService.completeById(orderId));
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Успешная отмена заказа")
    void cancelById_ValidOrderId_CancelsOrder() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);
        Mockito.when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.cancelById(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        Mockito.verify(warehouseService).addBookToWarehouse(warehouseId, bookId, quantity);
        Mockito.verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Исключение при отмене несуществующего заказа")
    void cancelById_NotExistingOrder_ThrowsException() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelById(orderId));
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Исключение при отмене заказа не в статусе CREATED")
    void cancelById_NotCreatedStatus_ThrowsException() {
        order.setOrderStatus(OrderStatus.COMPLETED);
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(ChangeOrderStatusException.class, () -> orderService.cancelById(orderId));
        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any(Order.class));
    }

    @Test
    @DisplayName("Получение заказа по ID")
    void findById_ExistingOrderId_ReturnsOrder() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.findById(orderId);

        assertNotNull(result);
        assertEquals(orderResponseDto, result);
        Mockito.verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Исключение при поиске несуществующего заказа")
    void findById_NotExistingOrder_ThrowsException() {
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findById(orderId));
    }

    @Test
    @DisplayName("Получение заказов по статусу")
    void findByOrderStatus_ValidStatus_ReturnsOrders() {
        Pageable pageable = Pageable.unpaged();
        Page<Order> ordersPage = new PageImpl<>(List.of(order));

        Mockito.when(orderRepository.findByOrderStatus(OrderStatus.CREATED, pageable)).thenReturn(ordersPage);
        Mockito.when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);

        Page<OrderResponseDto> result = orderService.findByOrderStatus(OrderStatus.CREATED, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderResponseDto, result.getContent().get(0));
        Mockito.verify(orderRepository).findByOrderStatus(OrderStatus.CREATED, pageable);
    }

    @Test
    @DisplayName("Получение заказов по дате")
    void findByOrderDateTimeBetween_ValidDates_ReturnsOrders() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        Pageable pageable = Pageable.unpaged();
        Page<Order> ordersPage = new PageImpl<>(List.of(order));

        Mockito.when(orderRepository.findByOrderDateTimeBetween(start, end, pageable)).thenReturn(ordersPage);
        Mockito.when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);

        Page<OrderResponseDto> result = orderService.findByOrderDateTimeBetween(start, end, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderResponseDto, result.getContent().get(0));
        Mockito.verify(orderRepository).findByOrderDateTimeBetween(start, end, pageable);
    }

    @Test
    @DisplayName("Получение заказов по складу")
    void findByWarehouseId_ValidWarehouseId_ReturnsOrders() {
        Pageable pageable = Pageable.unpaged();
        Page<Order> ordersPage = new PageImpl<>(List.of(order));

        Mockito.when(orderRepository.findByWarehouseId(warehouseId, pageable)).thenReturn(ordersPage);
        Mockito.when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);

        Page<OrderResponseDto> result = orderService.findByWarehouseId(warehouseId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderResponseDto, result.getContent().get(0));
        Mockito.verify(orderRepository).findByWarehouseId(warehouseId, pageable);
    }

    @Test
    @DisplayName("Получение заказов по складу и статусу")
    void findByWarehouseIdAndOrderStatus_ValidData_ReturnsOrders() {
        Pageable pageable = Pageable.unpaged();
        Page<Order> ordersPage = new PageImpl<>(List.of(order));

        Mockito.when(orderRepository.findByWarehouseIdAndOrderStatus(warehouseId, OrderStatus.CREATED, pageable)).thenReturn(ordersPage);
        Mockito.when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);

        Page<OrderResponseDto> result = orderService.findByWarehouseIdAndOrderStatus(warehouseId, OrderStatus.CREATED, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(orderResponseDto, result.getContent().get(0));
        Mockito.verify(orderRepository).findByWarehouseIdAndOrderStatus(warehouseId, OrderStatus.CREATED, pageable);
    }
}