package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ifellow.bookstore.advice.GlobalExceptionHandler;
import com.ifellow.bookstore.configuration.WebConfiguration;
import com.ifellow.bookstore.controller.OrderController;
import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.exception.OrderStatusException;
import com.ifellow.bookstore.exception.OrderException;
import com.ifellow.bookstore.service.api.OrderService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private final LocalDateTime testDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);
    private final BookOrderDto validBookOrder = new BookOrderDto(1L, 2);
    private final BookOrderDto invalidBookOrder = new BookOrderDto(0L, 0);

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/warehouses/{warehouseId}/orders - успешное создание заказа")
    void createOrder_ValidRequest_ReturnsCreatedOrder() throws Exception {
        OrderResponseDto responseDto = new OrderResponseDto(
                1L, 1L, testDateTime, OrderStatus.CREATED, 1L, BigDecimal.valueOf(500));
        when(orderService.create(anyLong(), any())).thenReturn(responseDto);

        ResultActions response = mockMvc.perform(post("/api/warehouses/{warehouseId}/orders", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(validBookOrder))));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderStatus").value("CREATED"))
                .andExpect(jsonPath("$.warehouseId").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(500));
    }

    @Test
    @DisplayName("POST /api/warehouses/{warehouseId}/orders - невалидные данные")
    void createOrder_InvalidRequest_ReturnsBadRequest() throws Exception {
        ResultActions response = mockMvc.perform(post("/api/warehouses/{warehouseId}/orders", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(invalidBookOrder))));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/orders/{orderId}/complete - успешное завершение заказа")
    void completeOrder_ValidId_ReturnsCompletedOrder() throws Exception {
        OrderResponseDto responseDto = new OrderResponseDto(
                1L, 1L, testDateTime, OrderStatus.COMPLETED, 1L, BigDecimal.valueOf(500));

        when(orderService.completeById(anyLong())).thenReturn(responseDto);

        ResultActions response = mockMvc.perform(post("/api/orders/{orderId}/complete", 1L));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("COMPLETED"));
    }

    @Test
    @DisplayName("POST /api/orders/{orderId}/complete - ошибка при неверном статусе")
    void completeOrder_InvalidStatus_ReturnsBadRequest() throws Exception {
        when(orderService.completeById(anyLong()))
                .thenThrow(new OrderStatusException("Invalid status"));

        ResultActions response = mockMvc.perform(post("/api/orders/{orderId}/complete", 1L));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/orders/{orderId}/cancel - успешная отмена заказа")
    void cancelOrder_ValidId_ReturnsCancelledOrder() throws Exception {
        OrderResponseDto responseDto = new OrderResponseDto(
                1L, 1L, testDateTime, OrderStatus.CANCELLED, 1L, BigDecimal.valueOf(500));
        when(orderService.cancelById(anyLong())).thenReturn(responseDto);

        ResultActions response = mockMvc.perform(post("/api/orders/{orderId}/cancel", 1L));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));
    }

    @Test
    @DisplayName("POST /api/orders/{orderId}/cancel - ошибка при неверном статусе")
    void cancelOrder_InvalidStatus_ReturnsBadRequest() throws Exception {
        when(orderService.cancelById(anyLong()))
                .thenThrow(new OrderStatusException("Invalid status"));

        ResultActions response = mockMvc.perform(post("/api/orders/{orderId}/cancel", 1L));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/orders/{orderId} - успешное получение заказа")
    void getOrder_ValidId_ReturnsOrder() throws Exception {
        OrderResponseDto responseDto = new OrderResponseDto(
                1L, 1L, testDateTime, OrderStatus.CREATED, 1L, BigDecimal.valueOf(500));
        when(orderService.findById(anyLong())).thenReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/api/orders/{orderId}", 1L));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderStatus").value("CREATED"));
    }

    @Test
    @DisplayName("GET /api/orders/{orderId} - заказ не найден")
    void getOrder_NotFound_ReturnsNotFound() throws Exception {
        when(orderService.findById(anyLong()))
                .thenThrow(new OrderException("Order not found"));

        ResultActions response = mockMvc.perform(get("/api/orders/{orderId}", 1L));

        response.andExpect(status().isNotFound());
    }
}