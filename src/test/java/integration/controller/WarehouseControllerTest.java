package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.AuthorRepository;
import com.ifellow.bookstore.repository.BookRepository;
import com.ifellow.bookstore.repository.GenreRepository;
import com.ifellow.bookstore.repository.WarehouseBookAmountRepository;
import com.ifellow.bookstore.repository.WarehouseRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = RootConfiguration.class)
public class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private WarehouseBookAmountRepository warehouseBookAmountRepository;
    @Autowired
    private JwtUtils jwtUtils;

    private String adminToken;
    private String managerToken;

    @BeforeEach
    public void setUp() {
        adminToken = jwtUtils.generateAccessTokenFromUsername("admin");
        managerToken = jwtUtils.generateAccessTokenFromUsername("manager");
    }

    @AfterEach
    public void tearDown() {
        warehouseBookAmountRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        warehouseRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание склада с валидными данными и ролью ADMIN")
    public void createValidDataAdminRoleCreatesWarehouse() throws Exception {
        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Москва, ул. Складская, 1");

        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warehouseRequestDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("Москва, ул. Складская, 1"));
    }

    @Test
    @DisplayName("Создание склада с ролью MANAGER - запрещено")
    public void createManagerRoleForbidden() throws Exception {
        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Москва, ул. Складская, 2");

        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warehouseRequestDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Получение склада по существующему ID")
    public void findByIdExistingIdReturnsWarehouse() throws Exception {
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());

        mockMvc.perform(get("/api/warehouses/" + warehouse.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(warehouse.getId()))
                .andExpect(jsonPath("$.address").value("Москва, ул. Складская, 1"));
    }

    @Test
    @DisplayName("Получение склада по несуществующему ID")
    public void findByIdNonExistingIdNotFound() throws Exception {
        String nonExistId = "123";
        mockMvc.perform(get("/api/warehouses/{nonExistId}", nonExistId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Добавление книги в запас склада с ролью MANAGER")
    public void addBookToWarehouseManagerRoleAddsBook() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());
        BookBulkDto bookBulkDto = new BookBulkDto(book.getId(), 5);

        mockMvc.perform(post("/api/warehouses/" + warehouse.getId() + "/stock/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookBulkDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удаление книги из запаса склада с ролью MANAGER")
    public void removeBookFromWarehouseManagerRoleRemovesBook() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());
        warehouseBookAmountRepository.save(WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build());
        BookBulkDto bookBulkDto = new BookBulkDto(book.getId(), 5);

        mockMvc.perform(post("/api/warehouses/" + warehouse.getId() + "/stock/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookBulkDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }
}