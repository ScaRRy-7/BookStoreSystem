package integration.servicerepository;

import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.service.api.AuthorService;
import integration.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthorServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private AuthorService authorService;

    AuthorRequestDto authorRequestDto;
    AuthorResponseDto authorResponseDto;

    @BeforeEach
    public void setUp() {
        authorRequestDto  = new AuthorRequestDto("Федор Достоевский");
        authorResponseDto = authorService.save(authorRequestDto);
    }


    @Test
    @DisplayName("Добавляет нового author с помощью RequestDto и возвращает ResponseDto")
    public void save_ValidRequestDto_ReturnsResponseDto() {
        assertNotNull(authorResponseDto);
        assertEquals(authorRequestDto.fullName(), authorResponseDto.fullName());
        assertNotNull(authorResponseDto.id());
    }

    @Test
    @DisplayName("Находит authorResponseDto по id")
    public void findById_ValidId_ReturnsResponseDto() {
        AuthorResponseDto acutalAuthorResponseDto = authorService.findById(authorResponseDto.id());

        assertNotNull(acutalAuthorResponseDto);
        assertEquals(authorResponseDto.fullName(), acutalAuthorResponseDto.fullName());
        assertNotNull(acutalAuthorResponseDto.id());
        assertEquals(authorResponseDto.id(), acutalAuthorResponseDto.id());
    }

    @Test
    @DisplayName("Находит модель Author по id")
    public void findAuthorById_ReturnsAuthor() throws InterruptedException {
        Author author = authorService.findAuthorById(authorResponseDto.id());

        assertNotNull(author);
        assertEquals(authorResponseDto.fullName(), author.getFullName());
        assertEquals(authorResponseDto.id(), author.getId());
    }
}