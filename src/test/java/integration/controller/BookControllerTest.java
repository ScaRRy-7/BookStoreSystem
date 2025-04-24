package integration.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.AuthorRepository;
import com.ifellow.bookstore.repository.BookRepository;
import com.ifellow.bookstore.repository.GenreRepository;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest(classes = {RootConfiguration.class})
@AutoConfigureMockMvc
@Transactional
class BookControllerTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    public void create_ValidJsonEntity_ReturnsBook() throws Exception {
        Author author = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre genre = genreRepository.save(new Genre(null, "Роман"));
        BookRequestDto bookRequestDto = new BookRequestDto("Мастер и Маргарита", author.getId(),genre.getId(), BigDecimal.valueOf(250L));

        ResultActions response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value(author.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genreId").value(genre.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(bookRequestDto.title()));
    }

    @Test
    public void findById_ValidId_ReturnsBook() throws Exception {
        Author author = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre genre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", author, genre, BigDecimal.valueOf(250L)));

        ResultActions response = mockMvc.perform(get("/api/books/{id}", savedBook.getId()));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value(author.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genreId").value(genre.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(savedBook.getTitle()));
    }
}