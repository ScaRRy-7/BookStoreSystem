package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.*;
import com.ifellow.bookstore.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = RootConfiguration.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private WarehouseBookAmountRepository warehouseBookAmountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private OrderItemRepository orderItemRepository;

    private String clientToken;
    private String managerToken;

    @BeforeEach
    public void setUp() {
        clientToken = jwtUtils.generateAccessTokenFromUsername("client");
        managerToken = jwtUtils.generateAccessTokenFromUsername("manager");
    }

    @AfterEach
    public void tearDown() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        warehouseBookAmountRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        warehouseRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание заказа с валидными данными и ролью CLIENT")
    public void create_ValidDataClientRole_CreatesOrder() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());
        warehouseBookAmountRepository.saveAndFlush(WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build());
        List<BookOrderDto> bookOrderDtos = List.of(new BookOrderDto(book.getId(), 5));

        mockMvc.perform(post("/api/warehouses/" + warehouse.getId() + "/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookOrderDtos))
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.warehouseId").value(warehouse.getId()))
                .andExpect(jsonPath("$.totalPrice").value(50.0));
    }

    @Test
    @DisplayName("Завершение заказа с ролью MANAGER")
    public void completeOrder_ManagerRole_CompletesOrder() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());
        User client = userRepository.findByUsername("client").orElseThrow();
        warehouseBookAmountRepository.saveAndFlush(WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build());
        Order order = Order.builder()
                .user(client)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(java.time.LocalDateTime.now())
                .build();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(post("/api/orders/" + order.getId() + "/complete")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("COMPLETED"));
    }

    @Test
    @DisplayName("Отмена заказа с ролью CLIENT")
    public void cancelOrder_ClientRole_CancelsOrder() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());
        User client = userRepository.findByUsername("client").orElseThrow();
        warehouseBookAmountRepository.saveAndFlush(WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build());
        Order order = Order.builder()
                .user(client)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(java.time.LocalDateTime.now())
                .build();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(post("/api/orders/" + order.getId() + "/cancel")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));
    }

    @Test
    @DisplayName("Отмена заказа с ролью MANAGER")
    public void cancelOrder_ManagerRole_CancelsOrder() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());
        User client = userRepository.findByUsername("client").orElseThrow();
        warehouseBookAmountRepository.saveAndFlush(WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build());
        Order order = Order.builder()
                .user(client)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(java.time.LocalDateTime.now())
                .build();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(post("/api/orders/" + order.getId() + "/cancel")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));
    }

    @Test
    @DisplayName("Получение заказа по ID с ролью MANAGER")
    public void findById_ManagerRole_ReturnsOrder() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());
        User client = userRepository.findByUsername("client").orElseThrow();
        warehouseBookAmountRepository.saveAndFlush(WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build());
        Order order = Order.builder()
                .user(client)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(java.time.LocalDateTime.now())
                .build();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/api/orders/" + order.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.warehouseId").value(warehouse.getId()))
                .andExpect(jsonPath("$.orderStatus").value("CREATED"));
    }

    @Test
    @DisplayName("Получение списка заказов с ролью MANAGER")
    public void findAll_ManagerRole_ReturnsOrders() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());
        User client = userRepository.findByUsername("client").orElseThrow();
        warehouseBookAmountRepository.saveAndFlush(WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build());
        Order order = Order.builder()
                .user(client)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(java.time.LocalDateTime.now())
                .build();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/api/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(order.getId()))
                .andExpect(jsonPath("$.content[0].warehouseId").value(warehouse.getId()))
                .andExpect(jsonPath("$.content[0].orderStatus").value("CREATED"));
    }
}