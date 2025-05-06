package integration.repository;

import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.model.Order;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.model.Warehouse;
import com.ifellow.bookstore.repository.OrderRepository;
import com.ifellow.bookstore.repository.UserRepository;
import com.ifellow.bookstore.repository.WarehouseRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = RootConfiguration.class)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Сохранение заказа с валидными данными")
    public void saveOrder_ValidData_PersistsData() {
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();
        userRepository.save(user);
        Warehouse warehouse = Warehouse.builder()
                .address("Москва, ул. Складская, 1")
                .build();
        warehouseRepository.save(warehouse);
        Order order = Order.builder()
                .user(user)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId());
        assertEquals(user, savedOrder.getUser());
        assertEquals(warehouse, savedOrder.getWarehouse());
        assertEquals(OrderStatus.CREATED, savedOrder.getOrderStatus());
    }

    @Test
    @DisplayName("Поиск заказа по ID")
    public void findById_ExistingId_ReturnsOrder() {
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();
        userRepository.save(user);
        Warehouse warehouse = Warehouse.builder()
                .address("Москва, ул. Складская, 1")
                .build();
        warehouseRepository.save(warehouse);
        Order order = Order.builder()
                .user(user)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(LocalDateTime.now())
                .build();
        entityManager.persist(order);

        Optional<Order> foundOrder = orderRepository.findById(order.getId());

        assertTrue(foundOrder.isPresent());
        assertEquals(user, foundOrder.get().getUser());
        assertEquals(OrderStatus.CREATED, foundOrder.get().getOrderStatus());
    }

    @Test
    @DisplayName("Поиск всех заказов")
    public void findAll_MultipleOrders_ReturnsAllOrders() {
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();
        userRepository.save(user);
        Warehouse warehouse = Warehouse.builder()
                .address("Москва, ул. Складская, 1")
                .build();
        warehouseRepository.save(warehouse);
        Order order1 = Order.builder()
                .user(user)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(LocalDateTime.now())
                .build();
        Order order2 = Order.builder()
                .user(user)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.COMPLETED)
                .totalPrice(BigDecimal.valueOf(100.0))
                .orderDateTime(LocalDateTime.now())
                .build();
        entityManager.persist(order1);
        entityManager.persist(order2);

        List<Order> orders = orderRepository.findAll();

        assertEquals(2, orders.size());
        assertTrue(orders.stream().anyMatch(o -> o.getOrderStatus() == OrderStatus.CREATED));
        assertTrue(orders.stream().anyMatch(o -> o.getOrderStatus() == OrderStatus.COMPLETED));
    }

    @Test
    @DisplayName("Удаление заказа")
    public void deleteOrder_ExistingOrder_RemovesData() {
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();
        userRepository.save(user);
        Warehouse warehouse = Warehouse.builder()
                .address("Москва, ул. Складская, 1")
                .build();
        warehouseRepository.save(warehouse);
        Order order = Order.builder()
                .user(user)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(LocalDateTime.now())
                .build();
        entityManager.persist(order);

        orderRepository.delete(order);
        Optional<Order> foundOrder = orderRepository.findById(order.getId());

        assertFalse(foundOrder.isPresent());
    }

    @Test
    @DisplayName("Поиск заказов по ID пользователя")
    public void findByUserId_ExistingUserId_ReturnsOrders() {
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();
        userRepository.save(user);
        Warehouse warehouse = Warehouse.builder()
                .address("Москва, ул. Складская, 1")
                .build();
        warehouseRepository.save(warehouse);
        Order order = Order.builder()
                .user(user)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(LocalDateTime.now())
                .build();
        entityManager.persist(order);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = orderRepository.findByUserId(user.getId(), pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals(order.getId(), page.getContent().get(0).getId());
    }
}