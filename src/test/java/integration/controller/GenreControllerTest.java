package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.GenreRepository;
import com.ifellow.bookstore.util.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = RootConfiguration.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private JwtUtils jwtUtils;

    private String managerToken;
    private String clientToken;

    @BeforeEach
    public void setUp() {
        managerToken = jwtUtils.generateAccessTokenFromUsername("manager");
        clientToken = jwtUtils.generateAccessTokenFromUsername("client");
    }

    @AfterEach
    public void tearDown() {
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание жанра с валидными данными и ролью MANAGER")
    public void create_ValidDataManagerRole_CreatesGenre() throws Exception {
        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");

        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreRequestDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Роман"));
    }

    @Test
    @DisplayName("Создание жанра с ролью CLIENT - запрещено")
    public void create_ClientRoleForbidden() throws Exception {
        GenreRequestDto genreRequestDto = new GenreRequestDto("Повесть");

        mockMvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genreRequestDto))
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Получение жанра по существующему ID")
    public void findById_ExistingId_ReturnsGenre() throws Exception {
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());

        mockMvc.perform(get("/api/genres/" + genre.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(genre.getId()))
                .andExpect(jsonPath("$.name").value("Роман"));
    }

    @Test
    @DisplayName("Получение жанра по несуществующему ID")
    public void findById_NonExistingId_NotFound() throws Exception {
        String nonExistId = "123";
        mockMvc.perform(get("/api/genres/{nonExistId}", nonExistId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}