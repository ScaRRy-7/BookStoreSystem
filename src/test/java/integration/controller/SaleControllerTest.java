package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.model.Sale;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.model.StoreBookAmount;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.repository.AuthorRepository;
import com.ifellow.bookstore.repository.BookRepository;
import com.ifellow.bookstore.repository.GenreRepository;
import com.ifellow.bookstore.repository.SaleItemRepository;
import com.ifellow.bookstore.repository.SaleRepository;
import com.ifellow.bookstore.repository.StoreBookAmountRepository;
import com.ifellow.bookstore.repository.StoreRepository;
import com.ifellow.bookstore.repository.UserRepository;
import com.ifellow.bookstore.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = RootConfiguration.class)
public class SaleControllerTest {

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
    private StoreRepository storeRepository;
    @Autowired
    private StoreBookAmountRepository storeBookAmountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String clientToken;
    private String managerToken;

    @BeforeEach
    public void setUp() {
        clientToken = jwtUtils.generateAccessTokenFromUsername("client");
        managerToken = jwtUtils.generateAccessTokenFromUsername("manager");
    }

    @AfterEach
    public void tearDown() {
        saleItemRepository.deleteAll();
        saleRepository.deleteAll();
        storeBookAmountRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        storeRepository.deleteAll();
    }

    @Test
    @DisplayName("Обработка продажи с валидными данными и ролью CLIENT")
    public void processSale_ValidDataClientRole_ProcessesSale() throws Exception {
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
        List<BookSaleDto> bookSaleDtos = List.of(new BookSaleDto(book.getId(), 5));

        mockMvc.perform(post("/api/stores/" + store.getId() + "/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookSaleDtos))
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(store.getId()))
                .andExpect(jsonPath("$.totalPrice").value(50.0));
    }

    @Test
    @DisplayName("Обработка продажи с ролью MANAGER - запрещено")
    public void processSale_ManagerRole_Forbidden() throws Exception {
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
        List<BookSaleDto> bookSaleDtos = List.of(new BookSaleDto(book.getId(), 5));

        mockMvc.perform(post("/api/stores/" + store.getId() + "/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookSaleDtos))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Получение продажи по ID с ролью MANAGER")
    public void findById_ManagerRole_ReturnsSale() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        User client = userRepository.findByUsername("client").orElseThrow();
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

        mockMvc.perform(get("/api/sales/" + sale.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sale.getId()))
                .andExpect(jsonPath("$.storeId").value(store.getId()))
                .andExpect(jsonPath("$.totalPrice").value(50.0));
    }

    @Test
    @DisplayName("Получение продажи по ID с ролью CLIENT - запрещено")
    public void findById_ClientRole_Forbidden() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        User client = userRepository.findByUsername("client").orElseThrow();
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

        mockMvc.perform(get("/api/sales/" + sale.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Получение списка продаж с ролью MANAGER")
    public void findAll_ManagerRole_ReturnsSales() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        User client = userRepository.findByUsername("client").orElseThrow();
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

        mockMvc.perform(get("/api/sales")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(sale.getId()))
                .andExpect(jsonPath("$.content[0].storeId").value(store.getId()))
                .andExpect(jsonPath("$.content[0].totalPrice").value(50.0));
    }
}