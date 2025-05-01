package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.configuration.SecurityConfiguration;
import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.TransferRequestDto;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = {RootConfiguration.class})
class TransferControllerTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private WarehouseBookAmountRepository warehouseBookAmountRepository;

    @Autowired
    private StoreBookAmountRepository storeBookAmountRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Перевозит книги со склада в магазин")
    public void transferBookFromWarehouseToStore() throws Exception {
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита",
                savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));
        Store savedStore = storeRepository.save(new Store(null, "Проспект Вернандского"));
        warehouseBookAmountRepository.save(new WarehouseBookAmount(null, savedWarehouse, savedBook, 10));
        TransferRequestDto transferRequestDto = new TransferRequestDto(savedWarehouse.getId(), savedStore.getId(),
                new BookBulkDto(savedBook.getId(), 10));

        ResultActions response = mockMvc.perform(post("/api/transfer/fromwarehousetostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequestDto)));

        response.andExpect(status().isOk());

    }

    @Test
    @DisplayName("Перевозит книги с магазина в магазин")
    public void transferBookFromStoreToStore() throws Exception {
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита",
                savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));
        Store savedStore1 = storeRepository.save(new Store(null, "Проспект Вернандского"));
        Store savedStore2 = storeRepository.save(new Store(null, "Фрунзенская"));
        storeBookAmountRepository.save(new StoreBookAmount(null, savedStore1, savedBook, 10));
        TransferRequestDto transferRequestDto = new TransferRequestDto(savedStore1.getId(), savedStore2.getId(),
                new BookBulkDto(savedBook.getId(), 10));

        ResultActions response = mockMvc.perform(post("/api/transfer/fromstoretostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequestDto)));

        response.andExpect(status().isOk());
    }

}