package unit.service;

import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.exception.GenreNotFoundException;
import com.ifellow.bookstore.mapper.GenreMapper;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.GenreRepository;
import com.ifellow.bookstore.service.impl.GenreServiceImpl;
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
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Long genreId;
    private Genre genre;
    private GenreRequestDto genreRequestDto;
    private GenreResponseDto genreResponseDto;

    @BeforeEach
    void setUp() {
        genreId = 1L;

        genre = new Genre();
        genre.setId(genreId);
        genre.setName("Фантастика");

        genreRequestDto = new GenreRequestDto("Фантастика");
        genreResponseDto = new GenreResponseDto(genreId, "Фантастика");
    }

    @Test
    @DisplayName("Успешное сохранение жанра")
    void save_ValidData_SavesGenre() {
        Mockito.when(genreMapper.toEntity(genreRequestDto)).thenReturn(genre);
        Mockito.when(genreRepository.save(genre)).thenReturn(genre);
        Mockito.when(genreMapper.toDto(genre)).thenReturn(genreResponseDto);

        GenreResponseDto result = genreService.save(genreRequestDto);

        assertNotNull(result);
        assertEquals(genreResponseDto, result);
        Mockito.verify(genreMapper).toEntity(genreRequestDto);
        Mockito.verify(genreRepository).save(genre);
        Mockito.verify(genreMapper).toDto(genre);
    }

    @Test
    @DisplayName("Успешный поиск жанра по ID")
    void findGenreById_ExistingId_ReturnsGenre() {
        Mockito.when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

        Genre result = genreService.findGenreById(genreId);

        assertNotNull(result);
        assertEquals(genre, result);
        Mockito.verify(genreRepository).findById(genreId);
    }

    @Test
    @DisplayName("Исключение при поиске несуществующего жанра по ID")
    void findGenreById_NotExistingId_ThrowsException() {
        Mockito.when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.findGenreById(genreId));
        Mockito.verify(genreRepository).findById(genreId);
    }

    @Test
    @DisplayName("Успешный поиск жанра с возвратом DTO по ID")
    void findById_ExistingId_ReturnsGenreResponseDto() {
        Mockito.when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        Mockito.when(genreMapper.toDto(genre)).thenReturn(genreResponseDto);

        GenreResponseDto result = genreService.findById(genreId);

        assertNotNull(result);
        assertEquals(genreResponseDto, result);
        Mockito.verify(genreRepository).findById(genreId);
        Mockito.verify(genreMapper).toDto(genre);
    }

    @Test
    @DisplayName("Исключение при поиске несуществующего жанра с возвратом DTO по ID")
    void findById_NotExistingId_ThrowsException() {
        Mockito.when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class, () -> genreService.findById(genreId));
        Mockito.verify(genreRepository).findById(genreId);
        Mockito.verify(genreMapper, Mockito.never()).toDto(Mockito.any(Genre.class));
    }
}