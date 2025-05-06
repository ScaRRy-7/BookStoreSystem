package integration.repository;

import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.AuthorRepository;
import com.ifellow.bookstore.repository.BookRepository;
import com.ifellow.bookstore.repository.GenreRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = RootConfiguration.class)
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Сохранение книги с валидными данными")
    public void saveBook_ValidData_PersistsData() {
        Author author = Author.builder().fullName("Михаил Булгаков").build();
        Genre genre = Genre.builder().name("Роман").build();
        authorRepository.save(author);
        genreRepository.save(genre);
        Book book = Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build();

        Book savedBook = bookRepository.save(book);

        assertNotNull(savedBook.getId());
        assertEquals("Мастер и Маргарита", savedBook.getTitle());
        assertEquals(author, savedBook.getAuthor());
        assertEquals(genre, savedBook.getGenre());
    }

    @Test
    @DisplayName("Поиск книги по ID")
    public void findById_ExistingId_ReturnsBook() {
        Author author = Author.builder().fullName("Михаил Булгаков").build();
        Genre genre = Genre.builder().name("Роман").build();
        authorRepository.save(author);
        genreRepository.save(genre);
        Book book = Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build();
        entityManager.persist(book);

        Optional<Book> foundBook = bookRepository.findById(book.getId());

        assertTrue(foundBook.isPresent());
        assertEquals("Мастер и Маргарита", foundBook.get().getTitle());
        assertEquals(author, foundBook.get().getAuthor());
    }

    @Test
    @DisplayName("Поиск всех книг")
    public void findAll_MultipleBooks_ReturnsAllBooks() {
        Author author = Author.builder().fullName("Михаил Булгаков").build();
        Genre genre = Genre.builder().name("Роман").build();
        authorRepository.save(author);
        genreRepository.save(genre);
        Book book1 = Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build();
        Book book2 = Book.builder()
                .title("Собачье сердце")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(8.0))
                .build();
        entityManager.persist(book1);
        entityManager.persist(book2);

        List<Book> books = bookRepository.findAll();

        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("Мастер и Маргарита")));
        assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("Собачье сердце")));
    }

    @Test
    @DisplayName("Удаление книги - удаляет данные из базы")
    public void deleteBook_ExistingBook_RemovesData() {
        Author author = Author.builder().fullName("Михаил Булгаков").build();
        Genre genre = Genre.builder().name("Роман").build();
        authorRepository.save(author);
        genreRepository.save(genre);
        Book book = Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build();
        entityManager.persist(book);

        bookRepository.delete(book);
        Optional<Book> foundBook = bookRepository.findById(book.getId());

        assertFalse(foundBook.isPresent());
    }

    @Test
    @DisplayName("Поиск книг по автору с использованием спецификации")
    public void findByAuthor_Specification_ReturnsBooks() {
        Author author = Author.builder().fullName("Михаил Булгаков").build();
        Genre genre = Genre.builder().name("Роман").build();
        authorRepository.save(author);
        genreRepository.save(genre);

        Book book = Book.builder()
                .title("Мастер и Маргарита")
                .author(author)
                .genre(genre)
                .price(BigDecimal.valueOf(10.0))
                .build();
        entityManager.persist(book);

        Specification<Book> spec = (root, query, cb) -> cb.equal(root.get("author").get("id"), author.getId());
        List<Book> books = bookRepository.findAll(spec);

        assertEquals(1, books.size());
        assertEquals("Мастер и Маргарита", books.get(0).getTitle());
    }
}