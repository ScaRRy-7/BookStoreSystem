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
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = RootConfiguration.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private BookRepository bookRepository;
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
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание книги с валидными данными и ролью MANAGER")
    public void create_ValidDataManagerRole_CreatesBook() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        BookRequestDto bookRequestDto = new BookRequestDto("Мастер и Маргарита", author.getId(), genre.getId(), BigDecimal.valueOf(10.0));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequestDto))
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Мастер и Маргарита"))
                .andExpect(jsonPath("$.authorId").value(author.getId()))
                .andExpect(jsonPath("$.genreId").value(genre.getId()))
                .andExpect(jsonPath("$.price").value(10.0));
    }

    @Test
    @DisplayName("Создание книги с ролью CLIENT - запрещено")
    public void create_ClientRoleForbidden() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        BookRequestDto bookRequestDto = new BookRequestDto("Мастер и Маргарита", author.getId(), genre.getId(), BigDecimal.valueOf(10.0));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequestDto))
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Получение книги по существующему ID")
    public void findById_ExistingId_ReturnsBook() throws Exception {
        Author author = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre = genreRepository.save(Genre.builder().name("Роман").build());
        Book book = bookRepository.save(Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build());

        mockMvc.perform(get("/api/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value("Мастер и Маргарита"))
                .andExpect(jsonPath("$.authorId").value(author.getId()))
                .andExpect(jsonPath("$.genreId").value(genre.getId()))
                .andExpect(jsonPath("$.price").value(10.0));
    }

    @Test
    @DisplayName("Получение книги по несуществующему ID")
    public void findById_NonExistingId_NotFound() throws Exception {
        String nonExistId = "123";
        mockMvc.perform(get("/api/books/{id}", nonExistId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение всех книг с фильтром по автору")
    public void findAll_WithFilter_ReturnsFilteredBooks() throws Exception {
        Author author1 = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Author author2 = authorRepository.save(Author.builder().fullName("Федор Достоевский").build());
        Genre genre1 = genreRepository.save(Genre.builder().name("Роман").build());
        bookRepository.save(Book.builder().title("Мастер и Маргарита").author(author1).genre(genre1).price(BigDecimal.valueOf(10.0)).build());
        bookRepository.save(Book.builder().title("Преступление и наказание").author(author2).genre(genre1).price(BigDecimal.valueOf(20.0)).build());

        mockMvc.perform(get("/api/books?authorId=" + author1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Мастер и Маргарита"))
                .andExpect(jsonPath("$.content[0].authorId").value(author1.getId()))
                .andExpect(jsonPath("$.content[0].genreId").value(genre1.getId()))
                .andExpect(jsonPath("$.content[0].price").value(10.0));
    }

    @Test
    @DisplayName("Получение всех книг с группировкой по жанру")
    public void findAll_WithGroupByGenre_ReturnsGroupedBooks() throws Exception {
        Author author1 = authorRepository.save(Author.builder().fullName("Михаил Булгаков").build());
        Genre genre1 = genreRepository.save(Genre.builder().name("Роман").build());
        Genre genre2 = genreRepository.save(Genre.builder().name("Повесть").build());
        bookRepository.save(Book.builder().title("Мастер и Маргарита").author(author1).genre(genre1).price(BigDecimal.valueOf(10.0)).build());
        bookRepository.save(Book.builder().title("Преступление и наказание").author(author1).genre(genre2).price(BigDecimal.valueOf(20.0)).build());
        bookRepository.save(Book.builder().title("Война и мир").author(author1).genre(genre1).price(BigDecimal.valueOf(15.0)).build());

        mockMvc.perform(get("/api/books?groupByGenre=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booksByGenre['Роман'][0].title").value("Мастер и Маргарита"))
                .andExpect(jsonPath("$.booksByGenre['Роман'][1].title").value("Война и мир"))
                .andExpect(jsonPath("$.booksByGenre['Повесть'][0].title").value("Преступление и наказание"));
    }
}