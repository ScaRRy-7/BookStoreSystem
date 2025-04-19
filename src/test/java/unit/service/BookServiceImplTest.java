package unit.service;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.exception.BookNotFoundException;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.api.BookRepository;
import com.ifellow.bookstore.service.api.AuthorService;
import com.ifellow.bookstore.service.api.GenreService;
import com.ifellow.bookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private GenreService genreService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Long bookId;
    private Long authorId;
    private Long genreId;
    private Book book;
    private Author author;
    private Genre genre;
    private BookRequestDto bookRequestDto;
    private BookResponseDto bookResponseDto;

    @BeforeEach
    void setUp() {
        bookId = 1L;
        authorId = 2L;
        genreId = 3L;

        author = new Author();
        author.setId(authorId);
        author.setFullName("Федор Достоевский");

        genre = new Genre();
        genre.setId(genreId);
        genre.setName("Роман");

        book = new Book();
        book.setId(bookId);
        book.setTitle("Преступление и наказание");
        book.setAuthor(author);
        book.setGenre(genre);
        book.setPrice(BigDecimal.valueOf(100));

        bookRequestDto = new BookRequestDto("Преступление и наказание", authorId, genreId, BigDecimal.valueOf(100));
        bookResponseDto = new BookResponseDto(bookId, "Преступление и наказание", authorId, genreId, BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Успешное сохранение книги")
    void save_ValidData_SavesBook() {
        Mockito.when(authorService.findAuthorById(authorId)).thenReturn(author);
        Mockito.when(genreService.findGenreById(genreId)).thenReturn(genre);
        Mockito.when(bookMapper.toEntity(bookRequestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        BookResponseDto result = bookService.save(bookRequestDto);

        assertNotNull(result);
        assertEquals(bookResponseDto, result);
        Mockito.verify(authorService).findAuthorById(authorId);
        Mockito.verify(genreService).findGenreById(genreId);
        Mockito.verify(bookMapper).toEntity(bookRequestDto);
        Mockito.verify(bookRepository).save(book);
        Mockito.verify(bookMapper).toResponseDto(book);
    }

    @Test
    @DisplayName("Успешный поиск книги по ID с возвратом DTO")
    void findById_ExistingId_ReturnsBookResponseDto() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        BookResponseDto result = bookService.findById(bookId);

        assertNotNull(result);
        assertEquals(bookResponseDto, result);
        Mockito.verify(bookRepository).findById(bookId);
        Mockito.verify(bookMapper).toResponseDto(book);
    }

    @Test
    @DisplayName("Исключение при поиске несуществующей книги с возвратом DTO")
    void findById_NotExistingId_ThrowsException() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findById(bookId));
        Mockito.verify(bookRepository).findById(bookId);
        Mockito.verify(bookMapper, Mockito.never()).toResponseDto(Mockito.any(Book.class));
    }

    @Test
    @DisplayName("Успешный поиск книги по ID")
    void findBookById_ExistingId_ReturnsBook() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        Book result = bookService.findBookById(bookId);

        assertNotNull(result);
        assertEquals(book, result);
        Mockito.verify(bookRepository).findById(bookId);
    }

    @Test
    @DisplayName("Исключение при поиске несуществующей книги")
    void findBookById_NotExistingId_ThrowsException() {
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findBookById(bookId));
        Mockito.verify(bookRepository).findById(bookId);
    }

    @Test
    @DisplayName("Поиск книг по жанру")
    void findByGenreId_ValidGenreId_ReturnsBooks() {
        Pageable pageable = Pageable.unpaged();
        Page<Book> booksPage = new PageImpl<>(List.of(book));

        Mockito.when(bookRepository.findByGenreId(genreId, pageable)).thenReturn(booksPage);
        Mockito.when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.findByGenreId(genreId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(bookResponseDto, result.getContent().get(0));
        Mockito.verify(bookRepository).findByGenreId(genreId, pageable);
        Mockito.verify(bookMapper).toResponseDto(book);
    }

    @Test
    @DisplayName("Поиск книг по автору")
    void findByAuthorId_ValidAuthorId_ReturnsBooks() {
        Pageable pageable = Pageable.unpaged();
        Page<Book> booksPage = new PageImpl<>(List.of(book));

        Mockito.when(bookRepository.findByAuthorId(authorId, pageable)).thenReturn(booksPage);
        Mockito.when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.findByAuthorId(authorId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(bookResponseDto, result.getContent().get(0));
        Mockito.verify(bookRepository).findByAuthorId(authorId, pageable);
        Mockito.verify(bookMapper).toResponseDto(book);
    }

    @Test
    @DisplayName("Поиск книг по названию")
    void findByTitle_ValidTitle_ReturnsBooks() {
        String title = "Преступление и наказание";
        Pageable pageable = Pageable.unpaged();
        Page<Book> booksPage = new PageImpl<>(List.of(book));

        Mockito.when(bookRepository.findByTitleContainingIgnoreCase(title, pageable)).thenReturn(booksPage);
        Mockito.when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.findByTitle(title, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(bookResponseDto, result.getContent().get(0));
        Mockito.verify(bookRepository).findByTitleContainingIgnoreCase(title, pageable);
        Mockito.verify(bookMapper).toResponseDto(book);
    }

    @Test
    @DisplayName("Поиск книг по имени автора")
    void findByAuthorFullName_ValidFullName_ReturnsBooks() {
        String fullName = "Федор Достоевский";
        Pageable pageable = Pageable.unpaged();
        Page<Book> booksPage = new PageImpl<>(List.of(book));

        Mockito.when(bookRepository.findByAuthorFullNameIgnoreCase(fullName, pageable)).thenReturn(booksPage);
        Mockito.when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.findByAuthorFullName(fullName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(bookResponseDto, result.getContent().get(0));
        Mockito.verify(bookRepository).findByAuthorFullNameIgnoreCase(fullName, pageable);
        Mockito.verify(bookMapper).toResponseDto(book);
    }

    @Test
    @DisplayName("Поиск книг по имени автора и названию")
    void findByAuthorFullNameAndTitle_ValidData_ReturnsBooks() {
        String fullName = "Федор Достоевский";
        String title = "Преступление и наказание";
        Pageable pageable = Pageable.unpaged();
        Page<Book> booksPage = new PageImpl<>(List.of(book));

        Mockito.when(bookRepository.findByAuthorFullNameIgnoreCaseAndTitleContainingIgnoreCase(fullName, title, pageable)).thenReturn(booksPage);
        Mockito.when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.findByAuthorFullNameAndTitle(fullName, title, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(bookResponseDto, result.getContent().get(0));
        Mockito.verify(bookRepository).findByAuthorFullNameIgnoreCaseAndTitleContainingIgnoreCase(fullName, title, pageable);
        Mockito.verify(bookMapper).toResponseDto(book);
    }

    @Test
    @DisplayName("Поиск всех книг с группировкой по жанру")
    void findAllGroupedByGenre_ValidRequest_ReturnsBooks() {
        Pageable pageable = Pageable.unpaged();
        Page<Book> booksPage = new PageImpl<>(List.of(book));

        Mockito.when(bookRepository.findAllOrderedByGenreAsc(pageable)).thenReturn(booksPage);
        Mockito.when(bookMapper.toResponseDto(book)).thenReturn(bookResponseDto);

        Page<BookResponseDto> result = bookService.findAllGroupedByGenre(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(bookResponseDto, result.getContent().get(0));
        Mockito.verify(bookRepository).findAllOrderedByGenreAsc(pageable);
        Mockito.verify(bookMapper).toResponseDto(book);
    }
}