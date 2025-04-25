package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.Media;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = {RootConfiguration.class})
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private StoreBookAmountRepository storeBookAmountRepository;

    @Test
    public void create_ValidJsonEntity_CreatesStore() throws Exception {
        StoreRequestDto storeRequestDto = new StoreRequestDto("Ул. Арбат");

        ResultActions response = mockMvc.perform(post("/api/stores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeRequestDto)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.address").value(storeRequestDto.address()));
    }

    @Test
    public void findById_ValidId_ReturnsStore() throws Exception {
        Store savedStore = storeRepository.save(new Store(null, "Ул. Арбат"));

        ResultActions response = mockMvc.perform(get("/api/stores/{id}", savedStore.getId()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.address").value(savedStore.getAddress()));
    }

    @Test
    public void addBooksToStore_ValidJsonEntity_AddsBooksToStore() throws Exception {
        Store savedStore = storeRepository.save(new Store(null, "Ул. Арбат"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита",
                savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        BookBulkDto bookBulkDto = new BookBulkDto(savedBook.getId(), 10);

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/add", savedStore.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isOk());
    }

    @Test
    public void removeBooksFromStore_ValidIdAndJsonEntity_RemovesBooksFromStore() throws Exception {
        Store savedStore = storeRepository.save(new Store(null, "Ул. Арбат"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита",
                savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        storeBookAmountRepository.save(new StoreBookAmount(null, savedStore, savedBook, 10));
        BookBulkDto bookBulkDto = new BookBulkDto(savedBook.getId(), 10);

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/remove", savedStore.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isOk());
    }

    @Test
    public void getStoreStock_ValidId_ReturnsStoreStock() throws Exception {
        Store savedStore = storeRepository.save(new Store(null, "Ул. Арбат"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита",
                savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        storeBookAmountRepository.save(new StoreBookAmount(null, savedStore, savedBook, 10));

        ResultActions response = mockMvc.perform(get("/api/stores/{id}/stock", savedStore.getId()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].storeId").value(savedStore.getId()))
                .andExpect(jsonPath("$.content[0].bookId").value(savedBook.getId()))
                .andExpect(jsonPath("$.content[0].quantity").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}