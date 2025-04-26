package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.advice.GlobalExceptionHandler;
import com.ifellow.bookstore.configuration.WebConfiguration;
import com.ifellow.bookstore.controller.TransferController;
import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.TransferRequestDto;
import com.ifellow.bookstore.exception.BookNotFoundException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreNotFoundException;
import com.ifellow.bookstore.exception.WarehouseNotFoundException;
import com.ifellow.bookstore.service.api.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
class TransferControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    private TransferRequestDto validTransferRequest;
    private TransferRequestDto invalidTransferRequest;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(transferController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        BookBulkDto validBookBulk = new BookBulkDto(1L, 5);
        BookBulkDto invalidBookBulk = new BookBulkDto(0L, 0);

        validTransferRequest = new TransferRequestDto(1L, 2L, validBookBulk);
        invalidTransferRequest = new TransferRequestDto(0L, 0L, invalidBookBulk);
    }

    @Test
    @DisplayName("POST /api/transfer/fromwarehousetostore - успешный трансфер со склада в магазин")
    public void transferFromWarehouseToStore_ValidRequest_ReturnsOk() throws Exception {
        doNothing().when(transferService).transferBookFromWarehouseToStore(
                anyLong(), anyLong(), anyLong(), anyInt());

        ResultActions response = mockMvc.perform(post("/api/transfer/fromwarehousetostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTransferRequest)));

        response.andExpect(status().isOk());
        verify(transferService, times(1)).transferBookFromWarehouseToStore(
                validTransferRequest.sourceId(),
                validTransferRequest.destinationId(),
                validTransferRequest.bookBulkDto().bookId(),
                validTransferRequest.bookBulkDto().quantity());
    }

    @Test
    @DisplayName("POST /api/transfer/fromwarehousetostore - невалидные данные")
    public void transferFromWarehouseToStore_InvalidRequest_ReturnsBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(post("/api/transfer/fromwarehousetostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTransferRequest)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/transfer/fromwarehousetostore - склад не найден")
    public void transferFromWarehouseToStore_WarehouseNotFound_ReturnsNotFound() throws Exception {
        doThrow(new WarehouseNotFoundException("Warehouse not found with id: 1"))
                .when(transferService).transferBookFromWarehouseToStore(anyLong(), anyLong(), anyLong(), anyInt());

        ResultActions response = mockMvc.perform(post("/api/transfer/fromwarehousetostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTransferRequest)));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/transfer/fromwarehousetostore - недостаточно книг на складе")
    public void transferFromWarehouseToStore_NotEnoughStock_ReturnsBadRequest() throws Exception {
        doThrow(new NotEnoughStockException("Not enough stock for book with id: 1"))
                .when(transferService).transferBookFromWarehouseToStore(anyLong(), anyLong(), anyLong(), anyInt());

        ResultActions response = mockMvc.perform(post("/api/transfer/fromwarehousetostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTransferRequest)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/transfer/fromstoretostore - успешный трансфер между магазинами")
    public void transferFromStoreToStore_ValidRequest_ReturnsOk() throws Exception {
        doNothing().when(transferService).transferBookFromStoreToStore(
                anyLong(), anyLong(), anyLong(), anyInt());

        ResultActions response = mockMvc.perform(post("/api/transfer/fromstoretostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTransferRequest)));

        response.andExpect(status().isOk());
        verify(transferService, times(1)).transferBookFromStoreToStore(
                validTransferRequest.sourceId(),
                validTransferRequest.destinationId(),
                validTransferRequest.bookBulkDto().bookId(),
                validTransferRequest.bookBulkDto().quantity());
    }

    @Test
    @DisplayName("POST /api/transfer/fromstoretostore - невалидные данные")
    public void transferFromStoreToStore_InvalidRequest_ReturnsBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(post("/api/transfer/fromstoretostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTransferRequest)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/transfer/fromstoretostore - магазин не найден")
    public void transferFromStoreToStore_StoreNotFound_ReturnsNotFound() throws Exception {
        doThrow(new StoreNotFoundException("Store not found with id: 1"))
                .when(transferService).transferBookFromStoreToStore(anyLong(), anyLong(), anyLong(), anyInt());

        ResultActions response = mockMvc.perform(post("/api/transfer/fromstoretostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTransferRequest)));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/transfer/fromstoretostore - недостаточно книг в магазине")
    public void transferFromStoreToStore_NotEnoughStock_ReturnsBadRequest() throws Exception {
        doThrow(new NotEnoughStockException("Not enough stock for book with id: 1"))
                .when(transferService).transferBookFromStoreToStore(anyLong(), anyLong(), anyLong(), anyInt());

        ResultActions response = mockMvc.perform(post("/api/transfer/fromstoretostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTransferRequest)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/transfer/fromstoretostore - книга не найдена")
    public void transferFromStoreToStore_BookNotFound_ReturnsNotFound() throws Exception {
        doThrow(new BookNotFoundException("Book not found with id: 1"))
                .when(transferService).transferBookFromStoreToStore(anyLong(), anyLong(), anyLong(), anyInt());

        ResultActions response = mockMvc.perform(post("/api/transfer/fromstoretostore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTransferRequest)));

        response.andExpect(status().isNotFound());
    }
}