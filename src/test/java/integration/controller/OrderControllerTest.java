package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.*;
import com.ifellow.bookstore.service.api.OrderService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = {RootConfiguration.class})
class OrderControllerTest {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private WarehouseBookAmountRepository warehouseBookAmountRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Создает заказ и возвращает Json")
    public void create_ValidWarehouseIdAndJsonEntity_CreatesOrder() throws Exception {
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        warehouseBookAmountRepository.save(new WarehouseBookAmount(null, savedWarehouse, savedBook, 10));

        ResultActions response = mockMvc.perform(post("/api/warehouses/{warehouseId}/orders", savedWarehouse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(new BookOrderDto(savedBook.getId(), 10)))));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.warehouseId").value(savedWarehouse.getId()))
                .andExpect(jsonPath("$.orderStatus").value("CREATED"))
                .andExpect(jsonPath("$.totalPrice").value(savedBook.getPrice().multiply(BigDecimal.valueOf(10))));
    }

    @Test
    @DisplayName("Выполняет указанный по id заказ и возвращает Json")
    public void completeById_ValidId_CompletesOrder() throws Exception {
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        warehouseBookAmountRepository.save(new WarehouseBookAmount(null, savedWarehouse, savedBook, 10));
        OrderResponseDto orderResponseDto = orderService.create(savedWarehouse.getId(), List.of(new BookOrderDto(savedBook.getId(), 10)));

        ResultActions response = mockMvc.perform(post("/api/orders/{orderId}/complete", orderResponseDto.id()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.totalPrice").value(savedBook.getPrice().multiply(BigDecimal.valueOf(10))))
                .andExpect(jsonPath("$.warehouseId").value(savedWarehouse.getId()));
    }

    @Test
    @DisplayName("Отменяет по указанному id заказ и возвращает Json")
    public void cancelById_ValidId_CancelsOrder() throws Exception {
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        warehouseBookAmountRepository.save(new WarehouseBookAmount(null, savedWarehouse, savedBook, 10));
        OrderResponseDto orderResponseDto = orderService.create(savedWarehouse.getId(), List.of(new BookOrderDto(savedBook.getId(), 10)));

        ResultActions response = mockMvc.perform(post("/api/orders/{orderId}/cancel", orderResponseDto.id()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELED"))
                .andExpect(jsonPath("$.totalPrice").value(savedBook.getPrice().multiply(BigDecimal.valueOf(10))))
                .andExpect(jsonPath("$.warehouseId").value(savedWarehouse.getId()));
    }

    @Test
    @DisplayName("Находит по указанному id заказ и возвращает Json")
    public void findById_ValidId_ReturnsOrder() throws Exception {
        Warehouse savedWarehouse = warehouseRepository.save(new Warehouse(null, "Ул. Арбат"));
        Genre savedGenre = genreRepository.save(new Genre(null, "Роман"));
        Author savedAuthor = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", savedAuthor, savedGenre, BigDecimal.valueOf(250)));
        warehouseBookAmountRepository.save(new WarehouseBookAmount(null, savedWarehouse, savedBook, 10));
        OrderResponseDto orderResponseDto = orderService.create(savedWarehouse.getId(), List.of(new BookOrderDto(savedBook.getId(), 10)));

        ResultActions response = mockMvc.perform(get("/api/orders/{orderId}", orderResponseDto.id()));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CREATED"))
                .andExpect(jsonPath("$.totalPrice").value(savedBook.getPrice().multiply(BigDecimal.valueOf(10))));
    }
}