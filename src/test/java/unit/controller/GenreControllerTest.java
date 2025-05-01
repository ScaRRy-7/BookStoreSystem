package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.advice.GlobalExceptionHandler;
import com.ifellow.bookstore.configuration.WebConfiguration;
import com.ifellow.bookstore.controller.GenreController;
import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.exception.GenreException;
import com.ifellow.bookstore.service.api.GenreService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {WebConfiguration.class})
class GenreControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreController genreController;

    private GenreRequestDto genreRequestDto;
    private GenreResponseDto genreResponseDto;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(genreController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        genreRequestDto = new GenreRequestDto("Роман");
        genreResponseDto = new GenreResponseDto(1L, "Роман");
    }

    @Test
    @DisplayName("POST /api/genres - успешно создает новый жанр")
    public void create_ValidRequest_ReturnsCreatedGenre() throws Exception {
        when(genreService.save(genreRequestDto)).thenReturn(genreResponseDto);

        ResultActions response = mockMvc.perform(post("/api/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(genreRequestDto)));

        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(genreResponseDto.id()))
                .andExpect(jsonPath("$.name").value(genreResponseDto.name()));
    }

    @Test
    @DisplayName("POST /api/genres - возвращает 400 при невалидных данных")
    public void create_InvalidRequest_ReturnsBadRequest() throws Exception {
        GenreRequestDto invalidDto = new GenreRequestDto("");

        ResultActions response = mockMvc.perform(post("/api/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/genres/{id} - успешно возвращает жанр по id")
    public void findById_ValidId_ReturnsFoundGenre() throws Exception {
        Long genreId = 1L;
        when(genreService.findById(genreId)).thenReturn(genreResponseDto);

        ResultActions response = mockMvc.perform(get("/api/genres/{id}", genreId));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(genreResponseDto.id()))
                .andExpect(jsonPath("$.name").value(genreResponseDto.name()));
    }

    @Test
    @DisplayName("GET /api/genres/{id} - возвращает 404 при отсутствии жанра")
    public void findById_InvalidId_ReturnsNotFound() throws Exception {
        Long genreId = 1L;
        when(genreService.findById(genreId))
                .thenThrow(new GenreException("Genre not found with id: " + genreId));

        ResultActions response = mockMvc.perform(get("/api/genres/{id}", genreId));

        response.andExpect(status().isNotFound());
    }
}