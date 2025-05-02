package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.advice.GlobalExceptionHandler;
import com.ifellow.bookstore.configuration.WebConfiguration;
import com.ifellow.bookstore.controller.StoreController;
import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreBookResponseDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.exception.BookException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreException;
import com.ifellow.bookstore.service.api.StoreService;
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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
class StoreControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StoreService storeService;

    @InjectMocks
    private StoreController storeController;

    private ObjectMapper objectMapper;
    private StoreRequestDto storeRequestDto;
    private StoreResponseDto storeResponseDto;
    private BookBulkDto bookBulkDto;
    private StoreBookResponseDto storeBookResponseDto;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders
                .standaloneSetup(storeController)
                .setControllerAdvice(globalExceptionHandler)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        storeRequestDto = new StoreRequestDto("ул. Пушкина, д. Колотушкина");
        storeResponseDto = new StoreResponseDto(1L, "ул. Пушкина, д. Колотушкина");
        bookBulkDto = new BookBulkDto(1L, 5);
        storeBookResponseDto = new StoreBookResponseDto(1L, 1L, 1L, 5);
    }

    @Test
    @DisplayName("POST /api/stores - успешно создает новый магазин")
    public void create_ValidRequest_ReturnsCreatedStore() throws Exception {
        when(storeService.save(storeRequestDto)).thenReturn(storeResponseDto);

        ResultActions response = mockMvc.perform(post("/api/stores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeRequestDto)));

        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(storeResponseDto.id()))
                .andExpect(jsonPath("$.address").value(storeResponseDto.address()));
    }

    @Test
    @DisplayName("POST /api/stores - возвращает 400 при невалидных данных")
    public void create_InvalidRequest_ReturnsBadRequest() throws Exception {
        StoreRequestDto invalidDto = new StoreRequestDto("");

        ResultActions response = mockMvc.perform(post("/api/stores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/stores/{id} - успешно возвращает магазин по id")
    public void findById_ValidId_ReturnsFoundStore() throws Exception {
        Long storeId = 1L;
        when(storeService.findById(storeId)).thenReturn(storeResponseDto);

        ResultActions response = mockMvc.perform(get("/api/stores/{id}", storeId));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(storeResponseDto.id()))
                .andExpect(jsonPath("$.address").value(storeResponseDto.address()));
    }

    @Test
    @DisplayName("GET /api/stores/{id} - возвращает 404 при отсутствии магазина")
    public void findById_InvalidId_Returns404() throws Exception {
        Long storeId = 999L;
        when(storeService.findById(storeId))
                .thenThrow(new StoreException("Store not found with id: " + storeId));

        ResultActions response = mockMvc.perform(get("/api/stores/{id}", storeId));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/stores/{id}/stock/add - успешно добавляет книги в магазин")
    public void addBooksToStore_ValidRequest_ReturnsOk() throws Exception {
        Long storeId = 1L;

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/add", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/stores/{id}/stock/add - возвращает 404 при отсутствии магазина")
    public void addBooksToStore_StoreNotFound_Returns404() throws Exception {
        Long storeId = 999L;
        doThrow(new StoreException("Store not found with id: " + storeId))
                .when(storeService).addBookToStore(storeId, new BookBulkDto(bookBulkDto.bookId(), bookBulkDto.quantity()));

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/add", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/stores/{id}/stock/add - возвращает 404 при отсутствии книги")
    public void addBooksToStore_BookNotFound_Returns404() throws Exception {
        Long storeId = 1L;
        doThrow(new BookException("Book not found with id: " + bookBulkDto.bookId()))
                .when(storeService).addBookToStore(storeId, new BookBulkDto(bookBulkDto.bookId(), bookBulkDto.quantity()));

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/add", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/stores/{id}/stock/add - возвращает 400 при невалидном количестве")
    public void addBooksToStore_InvalidQuantity_ReturnsBadRequest() throws Exception {
        Long storeId = 1L;
        doThrow(new IllegalArgumentException("quantity must be greater than zero"))
                .when(storeService).addBookToStore(storeId, new BookBulkDto(bookBulkDto.bookId(), bookBulkDto.quantity()));

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/add", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/stores/{id}/stock/remove - успешно удаляет книги из магазина")
    public void deleteBooksFromStore_ValidRequest_ReturnsOk() throws Exception {
        Long storeId = 1L;

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/remove", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/stores/{id}/stock/remove - возвращает 404 при отсутствии магазина")
    public void deleteBooksFromStore_StoreNotFound_Returns404() throws Exception {
        Long storeId = 999L;
        doThrow(new StoreException("Store not found with id: " + storeId))
                .when(storeService).removeBookFromStore(storeId, new BookBulkDto(bookBulkDto.bookId(), bookBulkDto.quantity()));

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/remove", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/stores/{id}/stock/remove - возвращает 404 при отсутствии книги")
    public void deleteBooksFromStore_BookNotFound_Returns404() throws Exception {
        Long storeId = 1L;
        doThrow(new BookException("Book not found with id: " + bookBulkDto.bookId()))
                .when(storeService).removeBookFromStore(storeId, new BookBulkDto(bookBulkDto.bookId(), bookBulkDto.quantity()));

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/remove", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/stores/{id}/stock/remove - возвращает 400 при недостаточном количестве книг")
    public void deleteBooksFromStore_NotEnoughStock_ReturnsBadRequest() throws Exception {
        Long storeId = 1L;
        doThrow(new NotEnoughStockException("Not enough stock of book with id: " + bookBulkDto.bookId()))
                .when(storeService).removeBookFromStore(storeId, new BookBulkDto(bookBulkDto.bookId(), bookBulkDto.quantity()));

        ResultActions response = mockMvc.perform(post("/api/stores/{id}/stock/remove", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookBulkDto)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/stores/{id}/stock - успешно возвращает остатки книг в магазине")
    public void getStoreStock_ValidRequest_ReturnsStoreStock() throws Exception {
        Long storeId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<StoreBookResponseDto> books = List.of(storeBookResponseDto);
        Page<StoreBookResponseDto> bookPage = new PageImpl<>(books, pageable, books.size());

        when(storeService.getStoreStock(storeId, pageable)).thenReturn(bookPage);

        ResultActions response = mockMvc.perform(get("/api/stores/{id}/stock", storeId)
                .param("page", "0")
                .param("size", "10"));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(storeBookResponseDto.id()))
                .andExpect(jsonPath("$.content[0].storeId").value(storeBookResponseDto.storeId()))
                .andExpect(jsonPath("$.content[0].bookId").value(storeBookResponseDto.bookId()))
                .andExpect(jsonPath("$.content[0].quantity").value(storeBookResponseDto.quantity()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}