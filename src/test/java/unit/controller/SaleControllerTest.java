package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ifellow.bookstore.advice.GlobalExceptionHandler;
import com.ifellow.bookstore.configuration.WebConfiguration;
import com.ifellow.bookstore.controller.SaleController;
import com.ifellow.bookstore.dto.filter.SaleFilter;
import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.exception.BookException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.SaleException;
import com.ifellow.bookstore.exception.StoreException;
import com.ifellow.bookstore.service.api.SaleService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
class SaleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SaleService saleService;

    @InjectMocks
    private SaleController saleController;

    private ObjectMapper objectMapper;
    private SaleResponseDto saleResponseDto;
    private List<BookSaleDto> bookSaleDtoList;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders
                .standaloneSetup(saleController)
                .setControllerAdvice(globalExceptionHandler)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        bookSaleDtoList = Arrays.asList(
                new BookSaleDto(1L, 2),
                new BookSaleDto(2L, 1)
        );

        saleResponseDto = new SaleResponseDto(
                1L,
                1L,
                LocalDateTime.now(),
                1L,
                BigDecimal.valueOf(500)
        );
    }

    @Test
    @DisplayName("POST /api/sales/process/{storeId} - успешная обработка продажи")
    public void processSale_ValidRequest_ReturnsProcessedSale() throws Exception {
        Long storeId = 1L;
        when(saleService.processSale(storeId, bookSaleDtoList)).thenReturn(saleResponseDto);

        ResultActions response = mockMvc.perform(post("/api/sales/process/{storeId}", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookSaleDtoList)));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(saleResponseDto.id()))
                .andExpect(jsonPath("$.totalPrice").value(saleResponseDto.totalPrice()))
                .andExpect(jsonPath("$.storeId").value(saleResponseDto.storeId()));
    }

    @Test
    @DisplayName("POST /api/sales/process/{storeId} - возвращает ошибку при отсутствии магазина")
    public void processSale_StoreNotFound_ReturnsNotFound() throws Exception {
        Long storeId = 999L;
        when(saleService.processSale(storeId, bookSaleDtoList))
                .thenThrow(new StoreException("Store not found with id: " + storeId));

        ResultActions response = mockMvc.perform(post("/api/sales/process/{storeId}", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookSaleDtoList)));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/sales/process/{storeId} - возвращает ошибку при отсутствии книги")
    public void processSale_BookNotFound_ReturnsNotFound() throws Exception {
        Long storeId = 1L;
        when(saleService.processSale(storeId, bookSaleDtoList))
                .thenThrow(new BookException("Book not found"));

        ResultActions response = mockMvc.perform(post("/api/sales/process/{storeId}", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookSaleDtoList)));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/sales/process/{storeId} - возвращает ошибку при недостаточном количестве книг")
    public void processSale_NotEnoughStock_ReturnsBadRequest() throws Exception {
        Long storeId = 1L;
        when(saleService.processSale(storeId, bookSaleDtoList))
                .thenThrow(new NotEnoughStockException("Not enough stock"));

        ResultActions response = mockMvc.perform(post("/api/sales/process/{storeId}", storeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookSaleDtoList)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/sales/{id} - успешно возвращает продажу по id")
    public void findById_ValidId_ReturnsFoundSale() throws Exception {
        Long saleId = 1L;
        when(saleService.findById(saleId)).thenReturn(saleResponseDto);

        ResultActions response = mockMvc.perform(get("/api/sales/{id}", saleId));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(saleResponseDto.id()))
                .andExpect(jsonPath("$.totalPrice").value(saleResponseDto.totalPrice()))
                .andExpect(jsonPath("$.storeId").value(saleResponseDto.storeId()));
    }

    @Test
    @DisplayName("GET /api/sales/{id} - возвращает 404 при отсутствии продажи")
    public void findById_InvalidId_Returns404() throws Exception {
        Long saleId = 1L;
        when(saleService.findById(saleId))
                .thenThrow(new SaleException("Sale not found with id: " + saleId));

        ResultActions response = mockMvc.perform(get("/api/sales/{id}", saleId));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/sales - фильтрует продажи по параметрам запроса")
    public void findAll_WithFilterParams_ReturnsFilteredSales() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        SaleFilter filter = new SaleFilter();
        filter.setStoreId(1L);
        filter.setStartTime(LocalDateTime.now().minusDays(7));
        filter.setEndTime(LocalDateTime.now());
        List<SaleResponseDto> sales = List.of(saleResponseDto);
        Page<SaleResponseDto> salePage = new PageImpl<>(sales, pageable, sales.size());
        when(saleService.findAll(any(SaleFilter.class), any(Pageable.class))).thenReturn(salePage);

        ResultActions response = mockMvc.perform(get("/api/sales")
                .param("storeId", filter.getStoreId().toString())
                .param("startTime", filter.getStartTime().toString())
                .param("endTime", filter.getEndTime().toString())
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(saleResponseDto.id()))
                .andExpect(jsonPath("$.content[0].totalPrice").value(saleResponseDto.totalPrice()))
                .andExpect(jsonPath("$.content[0].storeId").value(saleResponseDto.storeId()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}