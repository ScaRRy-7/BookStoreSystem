package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.filter.SaleFilter;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.*;
import com.ifellow.bookstore.service.api.SaleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = {RootConfiguration.class})
class SaleControllerTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private StoreBookAmountRepository storeBookAmountRepository;

    @Autowired
    private SaleService saleService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Совершает продажу и возвращает Json")
    public void processSale_ValidAndJsonEntity_ProcessesSale() throws Exception {
        Store savedStore = storeRepository.save(new Store(null, "Ул. Арбат"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", savedAuthor, savedGenre,
                BigDecimal.valueOf(250L)));
        storeBookAmountRepository.save(new StoreBookAmount(null, savedStore, savedBook, 10));
        BookSaleDto bookSaleDto = new BookSaleDto(savedBook.getId(), 10);

        ResultActions response = mockMvc.perform(post("/api/sales/process/{storeId}", savedStore.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(bookSaleDto))));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(savedStore.getId()))
                .andExpect(jsonPath("$.totalPrice").value(savedBook.getPrice().multiply(BigDecimal.valueOf(10))));
    }

    @Test
    @DisplayName("Находит продажу по указанному и возвращает Json")
    public void findById_ValidId_ReturnsSale() throws Exception {
        Store savedStore = storeRepository.save(new Store(null, "Ул. Арбат"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", savedAuthor, savedGenre,
                BigDecimal.valueOf(250L)));
        storeBookAmountRepository.save(new StoreBookAmount(null, savedStore, savedBook, 10));
        BookSaleDto bookSaleDto = new BookSaleDto(savedBook.getId(), 10);
        SaleResponseDto saleResponseDto = saleService.processSale(savedStore.getId(), List.of(bookSaleDto));

        ResultActions response = mockMvc.perform(get("/api/sales/{saleId}", saleResponseDto.id()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(savedStore.getId()))
                .andExpect(jsonPath("$.totalPrice").value(savedBook.getPrice().multiply(BigDecimal.valueOf(10))));
    }

    @Test
    @DisplayName("Находит все продажи по фильтру и возвращает Json")
    public void findAll_ValidJsonEntity_ReturnsSale() throws Exception {
        Store savedStore = storeRepository.save(new Store(null, "Ул. Арбат"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", savedAuthor, savedGenre,
                BigDecimal.valueOf(250L)));
        storeBookAmountRepository.save(new StoreBookAmount(null, savedStore, savedBook, 10));
        BookSaleDto bookSaleDto = new BookSaleDto(savedBook.getId(), 10);
        SaleResponseDto saleResponseDto = saleService.processSale(savedStore.getId(), List.of(bookSaleDto));

        ResultActions response = mockMvc.perform(get("/api/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new SaleFilter(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), savedStore.getId()))));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].storeId").value(savedStore.getId()))
                .andExpect(jsonPath("$.content[0].totalPrice").value(saleResponseDto.totalPrice()));
    }
}