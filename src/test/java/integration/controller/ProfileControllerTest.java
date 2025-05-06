package integration.controller;

import com.ifellow.bookstore.configuration.RootConfiguration;
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
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = RootConfiguration.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private WarehouseBookAmountRepository warehouseBookAmountRepository;
    @Autowired
    private StoreBookAmountRepository storeBookAmountRepository;
    @Autowired
    private JwtUtils jwtUtils;

    private String clientToken;

    @BeforeEach
    public void setUp() {
        clientToken = jwtUtils.generateAccessTokenFromUsername("client");
    }

    @AfterEach
    public void tearDown() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        saleItemRepository.deleteAll();
        saleRepository.deleteAll();
        warehouseBookAmountRepository.deleteAll();
        storeBookAmountRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        warehouseRepository.deleteAll();
        storeRepository.deleteAll();
    }

    @Test
    @DisplayName("Получение профиля с ролью CLIENT возвращает данные профиля")
    public void getProfile_ClientRole_ReturnsProfile() throws Exception {
        mockMvc.perform(get("/api/profile")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("client"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_CLIENT"));
    }

    @Test
    @DisplayName("Получение профиля без аутентификации возвращает ошибку")
    public void getProfile_NoAuthentication_ReturnsError() throws Exception {
        mockMvc.perform(get("/api/profile")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Получение заказов профиля с ролью CLIENT возвращает заказы")
    public void findProfile_OrdersClientRole_ReturnsOrders() throws Exception {
        User client = userRepository.findByUsername("client").orElseThrow();
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
        Order order = Order.builder()
                .user(client)
                .warehouse(warehouse)
                .orderStatus(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(50.0))
                .orderDateTime(LocalDateTime.now())
                .build();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get("/api/profile/my-orders")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(order.getId()))
                .andExpect(jsonPath("$.content[0].warehouseId").value(warehouse.getId()))
                .andExpect(jsonPath("$.content[0].orderStatus").value("CREATED"));
    }

    @Test
    @DisplayName("Получение продаж профиля с ролью CLIENT возвращает продажи")
    public void findProfileSales_ClientRole_ReturnsSales() throws Exception {
        User client = userRepository.findByUsername("client").orElseThrow();
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        storeBookAmountRepository.saveAndFlush(StoreBookAmount.builder()
                .store(store)
                .book(book)
                .amount(10)
                .build());
        Sale sale = Sale.builder()
                .user(client)
                .store(store)
                .saleDateTime(LocalDateTime.now())
                .totalPrice(BigDecimal.valueOf(50.0))
                .build();
        saleRepository.saveAndFlush(sale);

        mockMvc.perform(get("/api/profile/my-sales")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(sale.getId()))
                .andExpect(jsonPath("$.content[0].storeId").value(store.getId()))
                .andExpect(jsonPath("$.content[0].totalPrice").value(50.0));
    }
}