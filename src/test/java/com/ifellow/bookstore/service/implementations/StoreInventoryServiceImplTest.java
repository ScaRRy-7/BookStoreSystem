package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.StoreInventoryDao;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class StoreInventoryServiceImplTest {

    @Mock
    StoreInventoryDao storeInventoryDao;

    @InjectMocks
    StoreInventoryServiceImpl storeInventoryService;

    @Test
    @DisplayName("StoreInventoryService находит книги по указанному автору")
    public void findBooksByAuthor_ValidArgument_ReturnsListDto() {
        UUID storeId = UUID.randomUUID();
        String author = "Гоголь";
        Book book1 = new Book(
                "Ревизор", author, "Комедия", 500, 200, storeId);
        Book book2 = new Book(
                "Мёртвые души", author, "Роман", 500, 200, storeId);
        List<Book> actualBooks = List.of(book1, book2);
        Mockito.when(storeInventoryDao.findBooksByAuthor(author)).thenReturn(actualBooks);

        List<BookResponseDto> actualBooksDto = storeInventoryService.findBooksByAuthor(author);

        Assertions.assertEquals(2, actualBooksDto.size());
        Assertions.assertEquals(book1.getTitle(), actualBooksDto.get(0).title());
        Assertions.assertEquals(book2.getTitle(), actualBooksDto.get(1).title());
    }

    @Test
    @DisplayName("StoreInventoryService группирует книги по жанру с маппингом в Dto")
    public void groupBooksByGenre_ReturnsGroupedDtos() {
        UUID storeId = UUID.randomUUID();
        Book book1 = new Book(
                "Мастер и маргарита", "Булгаков", "Роман", 500, 200, storeId);
        Book book2 = new Book(
                "Мёртвые души", "Гоголь", "Роман", 500, 200, storeId);
        Mockito.when(storeInventoryDao.groupBooksByGenre()).thenReturn(Map.of("Роман", List.of(book1, book2)));

        Map<String, List<BookResponseDto>> actualBookDtos = storeInventoryService.groupBooksByGenre();

        Assertions.assertEquals(1, actualBookDtos.size());
        Assertions.assertEquals(2, actualBookDtos.get("Роман").size());
    }

    @Test
    @DisplayName("StoreInventoryService ")
    public void findBooksByTitle_ValidArgument_ReturnsListDto() {
        UUID storeId = UUID.randomUUID();
        String title = "Мастер и маргарита";
        Book book1 = new Book(
                title, "Булгаков", "Роман", 500, 200, storeId);
        Book book2 = new Book(
                title, "Булгаков", "Роман", 800, 400, storeId);
        Mockito.when(storeInventoryDao.findBooksByTitle(Mockito.any(String.class))).thenReturn(List.of(book1, book2));

        List<BookResponseDto> actualBookDtos = storeInventoryService.findBooksByTitle(title);

        Assertions.assertEquals(2, actualBookDtos.size());
        Assertions.assertEquals(book1.getTitle(), actualBookDtos.getFirst().title());
    }

}