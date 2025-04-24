package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = {RootConfiguration.class})
@AutoConfigureMockMvc
@Transactional
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void setUp() {
        authorRepository.deleteAll();
    }

    @Test
    public void create_ValidJsonEntity_CreatesAuthor() throws Exception {
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Михаил Булгаков");

        ResultActions response = mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorRequestDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value(authorRequestDto.fullName()));
    }

    @Test
    public void findById_ValidId_FindsAuthor() throws Exception {
        Author author = new Author(null, "Федор Достоевский");
        Author savedAuthor = authorRepository.save(author);

        ResultActions response = mockMvc.perform(get("/api/authors/{id}", savedAuthor.getId()));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value(savedAuthor.getFullName()));
    }
}