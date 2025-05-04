package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.repository.AuthorRepository;
import com.ifellow.bookstore.service.api.AuthorService;
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

@SpringBootTest(classes = RootConfiguration.class)
@AutoConfigureMockMvc
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;

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
        authorRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание автора с валидными данными и ролью MANAGER")
    public void createValidDataManagerRoleCreatesAuthor() throws Exception {
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Михаил Булгаков");

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequestDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Михаил Булгаков"));
    }

    @Test
    @DisplayName("Создание автора с ролью CLIENT - запрещено")
    public void createClientRoleForbidden() throws Exception {
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequestDto))
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Получение автора по существующему ID")
    public void findByIdExistingIdReturnsAuthor() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Лев Толстой").build());

        mockMvc.perform(get("/api/authors/" + author.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(author.getId()))
                .andExpect(jsonPath("$.fullName").value("Лев Толстой"));
    }

    @Test
    @DisplayName("Получение автора по несуществующему ID")
    public void findByIdNonExistingIdNotFound() throws Exception {
        mockMvc.perform(get("/api/authors/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}