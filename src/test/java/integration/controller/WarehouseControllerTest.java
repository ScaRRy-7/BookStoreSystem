package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.*;
import integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = {RootConfiguration.class})
public class WarehouseControllerTest {

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void create_ValidJsonEntity_CreatesWarehouse() throws Exception {
        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Ул. Арбат");

        ResultActions response = mockMvc.perform(post("/api/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(warehouseRequestDto)));

        response.andExpect(status().isCreated())
                .andExpect((jsonPath("$.id").isNumber()))
                .andExpect((jsonPath("$.address").value(warehouseRequestDto.address())));
    }

    @Test
    public void findById_ValidId_FindsWarehouse() throws Exception {
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));

        ResultActions response = mockMvc.perform(get("/api/warehouses/{id}", savedWarehouse.getId()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.address").value(savedWarehouse.getAddress()));
    }

    @Test
    public void addBooksToWarehouse_ValidIdAndJsonEntity_AddsBooksToWarehouse() throws Exception {
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита",
                savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));
        BookBulkDto bookBulkDto= new BookBulkDto(savedBook.getId(), 10);

        ResultActions response = mockMvc.perform(post("/api/warehouses/{id}/stock/add", savedWarehouse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isOk());
    }

    @Test
    public void removeBooksFromWarehouse_ValidIdAndJsonEntity_RemovesBooksFromWarehouse() throws Exception {
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита",
                savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));
        warehouseBookAmountRepository.save(new WarehouseBookAmount(null, savedWarehouse, savedBook, 10));
        BookBulkDto bookBulkDto = new BookBulkDto(savedBook.getId(), 10);

        ResultActions response = mockMvc.perform(post("/api/warehouses/{id}/stock/remove", savedWarehouse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isOk());
    }

    @Test
    public void getWarehouseStock_ValidId_ReturnsStock() throws Exception {
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита",
                savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));
        warehouseBookAmountRepository.save(new WarehouseBookAmount(null, savedWarehouse, savedBook, 10));

        ResultActions response = mockMvc.perform(get("/api/warehouses/{id}/stock", savedWarehouse.getId()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].warehouseId").value(savedWarehouse.getId()))
                .andExpect(jsonPath("$.content[0].bookId").value(savedBook.getId()))
                .andExpect(jsonPath("$.content[0].quantity").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
