package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.advice.GlobalExceptionHandler;
import com.ifellow.bookstore.configuration.WebConfiguration;
import com.ifellow.bookstore.controller.BookController;
import com.ifellow.bookstore.dto.filter.BookFilter;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.exception.BookNotFoundException;
import com.ifellow.bookstore.service.api.BookService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private ObjectMapper objectMapper;
    private BookRequestDto bookRequestDto;
    private BookResponseDto bookResponseDto;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders
                .standaloneSetup(bookController)
                .setControllerAdvice(globalExceptionHandler)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        bookRequestDto = new BookRequestDto(
                "Преступление и наказание", 1L, 1L, BigDecimal.valueOf(250L));

        bookResponseDto = new BookResponseDto(
                1L,"Преступление и наказание", 1L, 1L, BigDecimal.valueOf(250L));
    }

    @Test
    @DisplayName("POST /api/books - успешно создает новую книгу")
    public void create_ValidRequest_ReturnsCreatedBook() throws Exception {
        when(bookService.save(bookRequestDto)).thenReturn(bookResponseDto);

        ResultActions response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDto)));

        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookResponseDto.id()))
                .andExpect(jsonPath("$.title").value(bookResponseDto.title()))
                .andExpect(jsonPath("$.price").value(bookResponseDto.price()));
    }

    @Test
    @DisplayName("POST /api/books - возвращает 404 при невалидных данных")
    public void create_InvalidRequest_Returns404() throws Exception {
        BookRequestDto invalidDto = new BookRequestDto("", 0L, 0L, BigDecimal.valueOf(0L));

        ResultActions response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/books/{id} - успешно возвращает книгу по id")
    public void findById_ValidId_ReturnsFoundBook() throws Exception {
        Long bookId = 1L;
        when(bookService.findById(bookId)).thenReturn(bookResponseDto);

        ResultActions response = mockMvc.perform(get("/api/books/{id}", bookId));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookResponseDto.id()))
                .andExpect(jsonPath("$.title").value(bookResponseDto.title()))
                .andExpect(jsonPath("$.price").value(bookResponseDto.price()));
    }

    @Test
    @DisplayName("GET /api/books/{id} - возвращает 404 при отсутствии книги")
    public void findById_InvalidId_Returns404() throws Exception {
        Long bookId = 1L;
        when(bookService.findById(bookId))
                .thenThrow(new BookNotFoundException("Book not found with id: " + bookId));

        ResultActions response = mockMvc.perform(get("/api/books/{id}", bookId));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/books - фильтрует книги по параметрам запроса")
    public void findAll_WithFilterParams_ReturnsFilteredBooks() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        BookFilter filter = new BookFilter();
        filter.setTitle("преступление");
        filter.setAuthorFullName("достоевский");
        filter.setMinPrice(BigDecimal.valueOf(200));
        filter.setMaxPrice(BigDecimal.valueOf(600));
        List<BookResponseDto> books = List.of(bookResponseDto);
        Page<BookResponseDto> bookPage = new PageImpl<>(books, pageable, books.size());
        when(bookService.findAll(filter, pageable)).thenReturn(bookPage);

        ResultActions response = mockMvc.perform(get("/api/books")
                .param("title", filter.getTitle())
                .param("authorFullName", filter.getAuthorFullName())
                .param("minPrice", filter.getMinPrice().toString())
                .param("maxPrice", filter.getMaxPrice().toString())
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize())));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(bookResponseDto.id()))
                .andExpect(jsonPath("$.content[0].title").value(bookResponseDto.title()))
                .andExpect(jsonPath("$.content[0].price").value(bookResponseDto.price()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

    }
}