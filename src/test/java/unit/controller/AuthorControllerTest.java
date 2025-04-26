package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.advice.GlobalExceptionHandler;
import com.ifellow.bookstore.configuration.WebConfiguration;
import com.ifellow.bookstore.controller.AuthorController;
import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.exception.AuthorNotFoundException;
import com.ifellow.bookstore.service.api.AuthorService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
class AuthorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    private AuthorRequestDto authorRequestDto;
    private AuthorResponseDto authorResponseDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders
                .standaloneSetup(authorController)
                .setControllerAdvice(globalExceptionHandler)
                .build();

        authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        authorResponseDto = new AuthorResponseDto(1L, "Федор Достоевский");
    }

    @Test
    @DisplayName("GET /api/authors/{id} - успешно возвращает автора по id")
    public void findById_ReturnsAuthor() throws Exception {
        Long authorId = 1L;
        when(authorService.findById(authorId)).thenReturn(authorResponseDto);

        ResultActions response = mockMvc.perform(get("/api/authors/{id}", authorId));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(authorResponseDto.id()))
                .andExpect(jsonPath("$.fullName").value(authorResponseDto.fullName()));
    }

    @Test
    @DisplayName("GET /api/authors/{id} - возвращает ошибку 404 при отсутствии автора")
    public void findById_AuthorNotFound_Returns404() throws Exception {
        Long authorId = 1L;
        when(authorService.findById(authorId))
                .thenThrow(new AuthorNotFoundException("Author not found with id: " + authorId));

        ResultActions response = mockMvc.perform(get("/api/authors/{id}", authorId));

        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/authors - успешно создает нового автора")
    public void create_ValidRequest_ReturnsCreatedAuthor() throws Exception {
        when(authorService.save(authorRequestDto)).thenReturn(authorResponseDto);

        ResultActions response = mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorResponseDto)));

        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(authorResponseDto.id()))
                .andExpect(jsonPath("$.fullName").value(authorResponseDto.fullName()));
    }

    @Test
    @DisplayName("POST /api/authors - возвращает ошибку 400 при невалидных данных")
    public void create_InvalidRequest_ReturnsBadRequest() throws Exception {
        AuthorRequestDto invalidDto = new AuthorRequestDto("");

        ResultActions response = mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        response.andExpect(status().isBadRequest());
    }
}