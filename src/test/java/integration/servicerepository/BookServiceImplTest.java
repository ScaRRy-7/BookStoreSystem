package integration.servicerepository;

import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.service.api.AuthorService;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.GenreService;
import integration.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


class BookServiceImplTest extends AbstractIntegrationTest {

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

}