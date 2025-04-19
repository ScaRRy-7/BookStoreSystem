package integration;

import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.service.api.GenreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class GenreServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private GenreService genreService;

    GenreRequestDto genreRequestDto;

    @Test
    @DisplayName("Сохраняет genre и возвращает GenreResponseDto")
    public void save_ValidGenreRequestDto_ReturnsGenreResponseDto() {
        genreRequestDto = new GenreRequestDto("Роман");

        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);

        assertNotNull(genreResponseDto);
        assertEquals(genreRequestDto.name(), genreResponseDto.name());
    }

    @Test
    @DisplayName("Находит genre по id и возвращает genreResponseDto")
    public void findById_ValidGenreRequestDto_ReturnsGenreResponseDto() {
        genreRequestDto = new GenreRequestDto("Роман");
        GenreResponseDto expectedGenreResponseDto = genreService.save(genreRequestDto);

        GenreResponseDto actualGenreResponseDto = genreService.findById(expectedGenreResponseDto.id());

        assertNotNull(actualGenreResponseDto);
        assertEquals(expectedGenreResponseDto.name(), actualGenreResponseDto.name());
        assertEquals(expectedGenreResponseDto.id(), actualGenreResponseDto.id());
    }

    @Test
    @DisplayName("Находит genre по id и возвращает сущность Genre")
    public void findGenreById_ValidGenreRequestDto_ReturnsGenre() {
        genreRequestDto = new GenreRequestDto("Роман");
        GenreResponseDto expectedGenreResponseDto = genreService.save(genreRequestDto);

        Genre actualGenre = genreService.findGenreById(expectedGenreResponseDto.id());

        assertNotNull(actualGenre);
        assertEquals(expectedGenreResponseDto.id(), actualGenre.getId());
        assertEquals(expectedGenreResponseDto.name(), actualGenre.getName());
    }

}