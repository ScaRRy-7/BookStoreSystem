package integration.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.filter.BookFilter;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.AuthorRepository;
import com.ifellow.bookstore.repository.BookRepository;
import com.ifellow.bookstore.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    @DisplayName("Создает книгу и возвращает Json")
    public void create_ValidJsonEntity_CreatesBook() throws Exception {
        Author author = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre genre = genreRepository.save(new Genre(null, "Роман"));
        BookRequestDto bookRequestDto = new BookRequestDto("Мастер и Маргарита", author.getId(),genre.getId(), BigDecimal.valueOf(250L));

        ResultActions response = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequestDto)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.authorId").value(author.getId()))
                .andExpect(jsonPath("$.genreId").value(genre.getId()))
                .andExpect(jsonPath("$.title").value(bookRequestDto.title()));
    }

    @Test
    @DisplayName("Находит книгу по id и возвращает Json")
    public void findById_ValidId_ReturnsBook() throws Exception {
        Author author = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre genre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", author, genre, BigDecimal.valueOf(250L)));

        ResultActions response = mockMvc.perform(get("/api/books/{id}", savedBook.getId()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.authorId").value(author.getId()))
                .andExpect(jsonPath("$.genreId").value(genre.getId()))
                .andExpect(jsonPath("$.title").value(savedBook.getTitle()));
    }

    @Test
    @DisplayName("Находит все книги по фильтру и возвращает Json")
    public void findAll_ValidJsonEntity_ReturnsBook() throws Exception {
        Author author = authorRepository.save(new Author(null, "Михаил Булгаков"));
        Genre genre = genreRepository.save(new Genre(null, "Роман"));
        Book savedBook = bookRepository.save(new Book(null, "Мастер и Маргарита", author, genre, BigDecimal.valueOf(250L)));

        ResultActions response = mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                        .param("authorId", String.valueOf(author.getId()))
                        .param("genreId", String.valueOf(genre.getId()))
                        .param("title", "мастер")
                        .param("authorFullName", "михаил"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].authorId").value(author.getId()))
                .andExpect(jsonPath("$.content[0].genreId").value(genre.getId()))
                .andExpect(jsonPath("$.content[0].title").value(savedBook.getTitle()));
    }
}