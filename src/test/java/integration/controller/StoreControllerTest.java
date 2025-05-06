package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.AuthorRepository;
import com.ifellow.bookstore.repository.BookRepository;
import com.ifellow.bookstore.repository.GenreRepository;
import com.ifellow.bookstore.repository.StoreBookAmountRepository;
import com.ifellow.bookstore.repository.StoreRepository;
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
public class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private StoreBookAmountRepository storeBookAmountRepository;
    @Autowired
    private JwtUtils jwtUtils;

    private String adminToken;
    private String managerToken;
    private String clientToken;

    @BeforeEach
    public void setUp() {
        adminToken = jwtUtils.generateAccessTokenFromUsername("admin");
        managerToken = jwtUtils.generateAccessTokenFromUsername("manager");
        clientToken = jwtUtils.generateAccessTokenFromUsername("client");
    }

    @AfterEach
    public void tearDown() {
        storeBookAmountRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        storeRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание магазина с валидными данными и ролью admin")
    public void create_ValidDataAdminRole_CreatesStore() throws Exception {
        StoreRequestDto storeRequestDto = new StoreRequestDto("Москва, ул. Тверская, 1");

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("Москва, ул. Тверская, 1"));
    }

    @Test
    @DisplayName("Создание магазина с ролью CLIENT - запрещено")
    public void create_ClientRole_Forbidden() throws Exception {
        StoreRequestDto storeRequestDto = new StoreRequestDto("Москва, ул. Арбат, 10");

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeRequestDto))
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Получение магазина по существующему ID")
    public void findById_ExistingId_ReturnsStore() throws Exception {
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());

        mockMvc.perform(get("/api/stores/" + store.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(store.getId()))
                .andExpect(jsonPath("$.address").value("Москва, ул. Тверская, 1"));
    }

    @Test
    @DisplayName("Получение магазина по несуществующему ID")
    public void findById_NonExistingId_NotFound() throws Exception {
        String nonExistId = "123";
        mockMvc.perform(get("/api/stores/{123}", nonExistId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Добавление книги в запас магазина с ролью manager")
    public void addBookToStore_ManagerRole_AddsBook() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        BookBulkDto bookBulkDto = new BookBulkDto(book.getId(), 5);

        mockMvc.perform(post("/api/stores/" + store.getId() + "/stock/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookBulkDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удаление книги из запаса магазина с ролью manager")
    public void removeBookFromStore_ManagerRole_RemovesBook() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        storeBookAmountRepository.save(StoreBookAmount.builder()
                .store(store)
                .book(book)
                .amount(10)
                .build());
        BookBulkDto bookBulkDto = new BookBulkDto(book.getId(), 5);

        mockMvc.perform(post("/api/stores/" + store.getId() + "/stock/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookBulkDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение запасов магазина по существующему ID")
    public void getStoreStock_ExistingId_ReturnsStock() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        storeBookAmountRepository.save(StoreBookAmount.builder()
                .store(store)
                .book(book)
                .amount(10)
                .build());

        mockMvc.perform(get("/api/stores/" + store.getId() + "/stock")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].bookId").value(book.getId()))
                .andExpect(jsonPath("$.content[0].storeId").value(store.getId()))
                .andExpect(jsonPath("$.content[0].quantity").value(10));
    }

    @Test
    @DisplayName("Массовое добавление книг в запас магазина с ролью manager")
    public void bulkAddBooksToStore_ManagerRole_AddsBooks() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book1 = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Book book2 = bookRepository.save(Book.builder()
                .title("Собачье сердце")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(8.0))
                .build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        List<BookBulkDto> bookBulkDtos = List.of(
                new BookBulkDto(book1.getId(), 5),
                new BookBulkDto(book2.getId(), 3)
        );

        mockMvc.perform(post("/api/stores/" + store.getId() + "/stock/bulk-add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookBulkDtos))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Массовое удаление книг из запаса магазина с ролью manager")
    public void bulkRemoveBooksFromStore_ManagerRole_RemovesBooks() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book1 = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Book book2 = bookRepository.save(Book.builder()
                .title("Собачье сердце")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(8.0))
                .build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        storeBookAmountRepository.save(StoreBookAmount.builder()
                .store(store)
                .book(book1)
                .amount(10)
                .build());
        storeBookAmountRepository.save(StoreBookAmount.builder()
                .store(store)
                .book(book2)
                .amount(10)
                .build());
        List<BookBulkDto> bookBulkDtos = List.of(
                new BookBulkDto(book1.getId(), 5),
                new BookBulkDto(book2.getId(), 3)
        );

        mockMvc.perform(post("/api/stores/" + store.getId() + "/stock/bulk-remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookBulkDtos))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }
}