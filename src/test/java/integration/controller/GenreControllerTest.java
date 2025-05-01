package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.configuration.SecurityConfiguration;
import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.GenreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {RootConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class GenreControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("Создает жанр и возвращает Json")
    public void create_ValidJsonEntity_CreatesGenre() throws Exception {
        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");

        ResultActions response = mockMvc.perform(post("/api/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(genreRequestDto)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(genreRequestDto.name()));
    }

    @Test
    @DisplayName("Находит жанр по id и возвращает Json")
    public void findById_ValidId_ReturnsGenre() throws Exception {
        Genre genre = genreRepository.save(new Genre(null, "Роман"));

        ResultActions response = mockMvc.perform(get("/api/genres/{id}", genre.getId()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(genre.getName()));
    }

}