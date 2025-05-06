package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.TransferRequestDto;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.model.Warehouse;
import com.ifellow.bookstore.model.WarehouseBookAmount;
import com.ifellow.bookstore.model.StoreBookAmount;
import com.ifellow.bookstore.repository.AuthorRepository;
import com.ifellow.bookstore.repository.BookRepository;
import com.ifellow.bookstore.repository.GenreRepository;
import com.ifellow.bookstore.repository.StoreBookAmountRepository;
import com.ifellow.bookstore.repository.StoreRepository;
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
public class TransferControllerTest {

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
    private WarehouseRepository warehouseRepository;
    @Autowired
    private StoreBookAmountRepository storeBookAmountRepository;
    @Autowired
    private WarehouseBookAmountRepository warehouseBookAmountRepository;
    @Autowired
    private JwtUtils jwtUtils;

    private String managerToken;

    @BeforeEach
    public void setUp() {
        managerToken = jwtUtils.generateAccessTokenFromUsername("manager");
    }

    @AfterEach
    public void tearDown() {
        storeBookAmountRepository.deleteAll();
        warehouseBookAmountRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
        storeRepository.deleteAll();
        warehouseRepository.deleteAll();
    }

    @Test
    @DisplayName("Перенос книги со склада в магазин с ролью MANAGER")
    public void transferBookFromWarehouseToStore_ManagerRole_TransfersBook() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Warehouse warehouse = warehouseRepository.save(Warehouse.builder().address("Москва, ул. Складская, 1").build());
        Store store = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        warehouseBookAmountRepository.saveAndFlush(WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build());
        BookBulkDto bookBulkDto = new BookBulkDto(book.getId(), 5);
        TransferRequestDto transferRequestDto = new TransferRequestDto(warehouse.getId(), store.getId(), bookBulkDto);

        mockMvc.perform(post("/api/transfer/fromwarehousetostore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequestDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Перенос книги между магазинами с ролью MANAGER")
    public void transferBookFromStoreToStore_ManagerRole_TransfersBook() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());
        Store storeFrom = storeRepository.save(Store.builder().address("Москва, ул. Тверская, 1").build());
        Store storeTo = storeRepository.save(Store.builder().address("Москва, ул. Арбат, 10").build());
        storeBookAmountRepository.saveAndFlush(StoreBookAmount.builder()
                .store(storeFrom)
                .book(book)
                .amount(10)
                .build());
        BookBulkDto bookBulkDto = new BookBulkDto(book.getId(), 5);
        TransferRequestDto transferRequestDto = new TransferRequestDto(storeFrom.getId(), storeTo.getId(), bookBulkDto);

        mockMvc.perform(post("/api/transfer/fromstoretostore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequestDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());
    }
}