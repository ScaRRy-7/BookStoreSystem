package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.advice.GlobalExceptionHandler;
import com.ifellow.bookstore.configuration.WebConfiguration;
import com.ifellow.bookstore.controller.WarehouseController;
import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.dto.response.WarehouseResponseDto;
import com.ifellow.bookstore.exception.BookNotFoundException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.WarehouseNotFoundException;
import com.ifellow.bookstore.service.api.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
class WarehouseControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private WarehouseService warehouseService;

    @InjectMocks
    private WarehouseController warehouseController;

    private WarehouseRequestDto validWarehouseRequest;
    private WarehouseRequestDto invalidWarehouseRequest;
    private BookBulkDto validBookBulk;
    private BookBulkDto invalidBookBulk;
    private WarehouseResponseDto warehouseResponse;
    private WarehouseBookResponseDto warehouseBookResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(warehouseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        validWarehouseRequest = new WarehouseRequestDto("ул. Ленина, 10");
        invalidWarehouseRequest = new WarehouseRequestDto("");

        validBookBulk = new BookBulkDto(1L, 5);
        invalidBookBulk = new BookBulkDto(0L, 0);

        warehouseResponse = new WarehouseResponseDto(1L, "ул. Ленина, 10");
        warehouseBookResponse = new WarehouseBookResponseDto(1L, 1L, 1L, 10);
    }

    @Test
    @DisplayName("POST /api/warehouses - успешное создание склада")
    void createWarehouse_ValidRequest_ReturnsCreated() throws Exception {
        when(warehouseService.save(any(WarehouseRequestDto.class))).thenReturn(warehouseResponse);

        ResultActions response = mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validWarehouseRequest)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.address").value("ул. Ленина, 10"));

    }

    @Test
    @DisplayName("POST /api/warehouses - невалидный запрос (пустой адрес)")
    void createWarehouse_InvalidRequest_ReturnsBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidWarehouseRequest)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/warehouses/{id} - успешное получение склада")
    void getWarehouseById_ValidId_ReturnsWarehouse() throws Exception {
        when(warehouseService.findById(1L)).thenReturn(warehouseResponse);

        ResultActions response = mockMvc.perform(get("/api/warehouses/{id}", 1L));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.address").value("ул. Ленина, 10"));

    }

    @Test
    @DisplayName("GET /api/warehouses/{id} - склад не найден")
    void getWarehouseById_NotFound_ReturnsNotFound() throws Exception {
        when(warehouseService.findById(1L))
                .thenThrow(new WarehouseNotFoundException("Warehouse not found"));

        ResultActions response = mockMvc.perform(get("/api/warehouses/{id}", 1L));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/warehouses/{id}/stock/add - успешное добавление книг")
    void addBooksToWarehouse_ValidRequest_ReturnsOk() throws Exception {
        doNothing().when(warehouseService).addBookToWarehouse(1L, 1L, 5);

        ResultActions response = mockMvc.perform(post("/api/warehouses/{id}/stock/add", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookBulk)));

        response.andExpect(status().isOk());

    }

    @Test
    @DisplayName("POST /api/warehouses/{id}/stock/add - невалидные данные (количество = 0)")
    void addBooksToWarehouse_InvalidQuantity_ReturnsBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(post("/api/warehouses/{id}/stock/add", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBookBulk)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/warehouses/{id}/stock/add - книга не найдена")
    void addBooksToWarehouse_BookNotFound_ReturnsNotFound() throws Exception {
        doThrow(new BookNotFoundException("Book not found"))
                .when(warehouseService).addBookToWarehouse(1L, 1L, 5);

        ResultActions response = mockMvc.perform(post("/api/warehouses/{id}/stock/add", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookBulk)));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/warehouses/{id}/stock/remove - успешное удаление книг")
    void removeBooksFromWarehouse_ValidRequest_ReturnsOk() throws Exception {
        doNothing().when(warehouseService).removeBookFromWarehouse(1L, 1L, 5);

        ResultActions response = mockMvc.perform(post("/api/warehouses/{id}/stock/remove", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookBulk)));

        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/warehouses/{id}/stock/remove - недостаточно книг на складе")
    void removeBooksFromWarehouse_NotEnoughStock_ReturnsBadRequest() throws Exception {
        doThrow(new NotEnoughStockException("Not enough stock"))
                .when(warehouseService).removeBookFromWarehouse(1L, 1L, 5);

        ResultActions response = mockMvc.perform(post("/api/warehouses/{id}/stock/remove", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validBookBulk)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/warehouses/{id}/stock - успешное получение списка книг")
    void getWarehouseStock_ValidRequest_ReturnsStock() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WarehouseBookResponseDto> page = new PageImpl<>(List.of(warehouseBookResponse), pageable, 1);

        when(warehouseService.getWarehouseStock(1L, pageable)).thenReturn(page);

        ResultActions response = mockMvc.perform(get("/api/warehouses/{id}/stock", 1L)
                        .param("page", "0")
                        .param("size", "10"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].warehouseId").value(1L))
                .andExpect(jsonPath("$.content[0].bookId").value(1L))
                .andExpect(jsonPath("$.content[0].quantity").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

    }

    @Test
    @DisplayName("GET /api/warehouses/{id}/stock - склад не найден")
    void getWarehouseStock_WarehouseNotFound_ReturnsNotFound() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        when(warehouseService.getWarehouseStock(1L, pageable))
                .thenThrow(new WarehouseNotFoundException("Warehouse not found"));

        ResultActions response = mockMvc.perform(get("/api/warehouses/{id}/stock", 1L)
                        .param("page", "0")
                        .param("size", "10"));

        response.andExpect(status().isNotFound());


    }
}