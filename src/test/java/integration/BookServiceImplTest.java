package integration;

import com.ifellow.bookstore.AppConfig;
import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.service.api.AuthorService;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.GenreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = AppConfig.class)
class BookServiceImplTest {

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private GenreService genreService;

    AuthorRequestDto authorRequestDto;
    AuthorResponseDto authorResponseDto;

    GenreRequestDto genreRequestDto;
    GenreResponseDto genreResponseDto;

    BookRequestDto bookRequestDto;
    BookResponseDto bookResponseDto;

    @Test
    @DisplayName("сохраняет новую сущность Book")
    public void save_validRequestDto_SavesBookAndReturnsResponseDto() {
        authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        authorResponseDto = authorService.save(authorRequestDto);
        genreRequestDto = new GenreRequestDto("Роман");
        genreResponseDto = genreService.save(genreRequestDto);
        bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));

        bookResponseDto = bookService.save(bookRequestDto);

        assertNotNull(bookResponseDto);
        assertEquals(authorResponseDto.id(), bookResponseDto.authorId());
        assertEquals(genreResponseDto.id(), bookResponseDto.genreId());
    }

    @Test
    @DisplayName("находит сохраненную сущность Book и возвращает ResponseDto")
    public void findById_ValidId_ReturnsResponseDto() {
        authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        authorResponseDto = authorService.save(authorRequestDto);
        genreRequestDto = new GenreRequestDto("Роман");
        genreResponseDto = genreService.save(genreRequestDto);
        bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        bookResponseDto = bookService.save(bookRequestDto);

        BookResponseDto actualBookResponseDto = bookService.findById(bookResponseDto.id());

        assertNotNull(actualBookResponseDto);
        assertEquals(authorResponseDto.id(), actualBookResponseDto.authorId());
        assertEquals(genreResponseDto.id(), actualBookResponseDto.genreId());
    }

    @Test
    @DisplayName("находит сущность Book по genreId и возвращает Page из BookResponseDto объектов")
    public void findByGenreId_ValidId_ReturnsPageOfResponseDtos() {
        authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        authorResponseDto = authorService.save(authorRequestDto);
        genreRequestDto = new GenreRequestDto("Роман");
        genreResponseDto = genreService.save(genreRequestDto);
        bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        bookResponseDto = bookService.save(bookRequestDto);

        Page<BookResponseDto> actualBookResponseDtos = bookService.findByGenreId(genreResponseDto.id(), PageRequest.of(0, 1));

        assertNotNull(actualBookResponseDtos);
        assertEquals(genreResponseDto.id(), actualBookResponseDtos.iterator().next().genreId());
    }

    @Test
    @DisplayName("находит сущность Book по authorId и возвращает Page из BookResponseDto объектов")
    public void findByAuthorId_ValidId_ReturnsPageOfResponseDtos() {
        authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        authorResponseDto = authorService.save(authorRequestDto);
        genreRequestDto = new GenreRequestDto("Роман");
        genreResponseDto = genreService.save(genreRequestDto);
        bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        bookResponseDto = bookService.save(bookRequestDto);

        Page<BookResponseDto> actualBookResponseDtos = bookService.findByAuthorId(authorResponseDto.id(), PageRequest.of(0, 1));

        assertNotNull(actualBookResponseDtos);
        assertEquals(authorResponseDto.id(), actualBookResponseDtos.iterator().next().authorId());
    }

    @Test
    @DisplayName("находит сущность Book по title и возвращает Page из BookResponseDto объектов")
    public void findByTitle_ValidTitle_ReturnsPageOfResponseDtos() {
        authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        authorResponseDto = authorService.save(authorRequestDto);
        genreRequestDto = new GenreRequestDto("Роман");
        genreResponseDto = genreService.save(genreRequestDto);
        bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        bookResponseDto = bookService.save(bookRequestDto);

        Page<BookResponseDto> actualBookResponseDtos = bookService.findByTitle(bookResponseDto.title(), PageRequest.of(0, 1));

        assertNotNull(actualBookResponseDtos);
        assertEquals(bookResponseDto.title(), actualBookResponseDtos.iterator().next().title());
    }

    @Test
    @DisplayName("находит сущность Book по Author full name и возвращает Page из BookResponseDto объектов")
    public void findByAuthorFullName_ValidFullName_ReturnsPageOfResponseDtos() {
        authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        authorResponseDto = authorService.save(authorRequestDto);
        genreRequestDto = new GenreRequestDto("Роман");
        genreResponseDto = genreService.save(genreRequestDto);
        bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        bookResponseDto = bookService.save(bookRequestDto);

        Page<BookResponseDto> actualBookResponseDtos = bookService.findByAuthorFullName(authorResponseDto.fullName(), PageRequest.of(0, 1));

        assertNotNull(actualBookResponseDtos);
        assertEquals(authorResponseDto.id(), actualBookResponseDtos.iterator().next().authorId());
    }


}