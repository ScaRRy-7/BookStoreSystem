package unit.service;

import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.exception.AuthorNotFoundException;
import com.ifellow.bookstore.mapper.AuthorMapper;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.repository.api.AuthorRepository;
import com.ifellow.bookstore.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Long authorId;
    private Author author;
    private AuthorRequestDto authorRequestDto;
    private AuthorResponseDto authorResponseDto;

    @BeforeEach
    void setUp() {
        authorId = 1L;

        author = new Author();
        author.setId(authorId);
        author.setFullName("Федор Достоевский");

        authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        authorResponseDto = new AuthorResponseDto(authorId, "Федор Достоевский");
    }

    @Test
    @DisplayName("Успешное сохранение автора")
    void save_ValidData_SavesAuthor() {
        Mockito.when(authorMapper.toEntity(authorRequestDto)).thenReturn(author);
        Mockito.when(authorRepository.save(author)).thenReturn(author);
        Mockito.when(authorMapper.toResponseDto(author)).thenReturn(authorResponseDto);

        AuthorResponseDto result = authorService.save(authorRequestDto);

        assertNotNull(result);
        assertEquals(authorResponseDto, result);
        Mockito.verify(authorMapper).toEntity(authorRequestDto);
        Mockito.verify(authorRepository).save(author);
        Mockito.verify(authorMapper).toResponseDto(author);
    }

    @Test
    @DisplayName("Успешный поиск автора по ID с возвратом DTO")
    void findById_ExistingId_ReturnsAuthorResponseDto() {
        Mockito.when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));
        Mockito.when(authorMapper.toResponseDto(author)).thenReturn(authorResponseDto);

        AuthorResponseDto result = authorService.findById(authorId);

        assertNotNull(result);
        assertEquals(authorResponseDto, result);
        Mockito.verify(authorRepository).findById(authorId);
        Mockito.verify(authorMapper).toResponseDto(author);
    }

    @Test
    @DisplayName("Исключение при поиске несуществующего автора с возвратом DTO")
    void findById_NotExistingId_ThrowsException() {
        Mockito.when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.findById(authorId));
        Mockito.verify(authorRepository).findById(authorId);
        Mockito.verify(authorMapper, Mockito.never()).toResponseDto(Mockito.any(Author.class));
    }

    @Test
    @DisplayName("Успешный поиск автора по ID")
    void findAuthorById_ExistingId_ReturnsAuthor() {
        Mockito.when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        Author result = authorService.findAuthorById(authorId);

        assertNotNull(result);
        assertEquals(author, result);
        Mockito.verify(authorRepository).findById(authorId);
    }

    @Test
    @DisplayName("Исключение при поиске несуществующего автора")
    void findAuthorById_NotExistingId_ThrowsException() {
        Mockito.when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(AuthorNotFoundException.class, () -> authorService.findAuthorById(authorId));
        Mockito.verify(authorRepository).findById(authorId);
    }
}