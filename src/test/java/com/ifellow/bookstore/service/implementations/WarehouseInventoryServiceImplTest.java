package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.WarehouseInventoryDao;
import com.ifellow.bookstore.dto.request.BookRequestDto;
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
class WarehouseInventoryServiceImplTest {

    @Mock
    private WarehouseInventoryDao warehouseInventoryDao;

    @InjectMocks
    private WarehouseInventoryServiceImpl warehouseInventoryService;
    private final UUID WAREHOUSE_ID = null;

    @Test
    @DisplayName("WarehouseInventoryService добавляет Book с помощью корректного Dto")
    public void addBook_CorrectArgument_callWarehouseInventoryDao() {
        BookRequestDto bookRequestDto = new BookRequestDto(
                "Мастер и Маргарита", "Михаил Булгаков", "Роман", 500, 200);

        warehouseInventoryService.addBook(bookRequestDto);

        Mockito.verify(warehouseInventoryDao, Mockito.times(1)).addBook(Mockito.any(Book.class));
    }

    @Test
    @DisplayName("WarehouseInventoryService удаляет Book с помощью корректного Dto")
    public void removeBook_CorrectArgument_callWarehouseInventoryDao() {
        BookRequestDto bookRequestDto = new BookRequestDto(
                "Мастер и Маргарита", "Михаил Булгаков", "Роман", 500, 200);

        warehouseInventoryService.removeBook(bookRequestDto);

        Mockito.verify(warehouseInventoryDao, Mockito.times(1)).removeBook(Mockito.any(Book.class));
    }

    @Test
    @DisplayName("WarehouseInventoryService получает корректный аргумент и возвращает список ResponseDto")
    public void findBooksByAuthor_CorrectArgument_ReturnMappedDtos() {
        String author = "Михаил Булгаков";
        Book book = new Book(
                "Мастер и маргарита", author, "Роман", 500, 200, WAREHOUSE_ID);
        Mockito.when(warehouseInventoryDao.findBooksByAuthor(author)).thenReturn(List.of(book));


        List<BookResponseDto> books = warehouseInventoryService.findBooksByAuthor(author);


        Assertions.assertNotNull(books);
        Assertions.assertEquals(1, books.size());
        Assertions.assertEquals(book.getTitle(), books.getFirst().title());
    }

    @Test
    @DisplayName("WarehouseInventoryService группирует книги по жанру с маппингом в Dto")
    public void groupBooksByGenre_ReturnGroupedDtos() {
        Book book1 = new Book(
                "Мастер и маргарита", "Булгаков", "Роман", 500, 200, WAREHOUSE_ID);
        Book book2 = new Book(
                "Мёртвые души", "Гоголь", "Роман", 500, 200, WAREHOUSE_ID);
        Mockito.when(warehouseInventoryDao.groupBooksByGenre()).thenReturn(Map.of("Роман", List.of(book1, book2)));


        Map<String, List<BookResponseDto>> actualBooks = warehouseInventoryService.groupBooksByGenre();


        Assertions.assertNotNull(actualBooks);
        Assertions.assertEquals(2, actualBooks.get("Роман").size());
    }

    @Test
    @DisplayName("WarehouseInventoryService получает валидный аргумент и удаляет список книг со склада")
    public void removeBooks_ValidArgument_callWarehouseInventoryDao() {
        Book book1 = new Book(
                "Мастер и маргарита", "Булгаков", "Роман", 500, 200, WAREHOUSE_ID);
        List<Book> booksToRemove = List.of(book1);


        warehouseInventoryService.removeBooks(booksToRemove);


        Mockito.verify(warehouseInventoryDao, Mockito.times(1)).removeBooks(booksToRemove);
    }

    @Test
    @DisplayName("WarehouseInventoryService получает некорректный аргумент и выбрасывает исключение")
    public void removeBooks_InvalidArgument_throwsException() {
        Book book1 = new Book(
                "Мастер и маргарита", "Булгаков", "Роман", 500, 200, UUID.randomUUID());
        List<Book> booksToRemove = List.of(book1);


        Assertions.assertThrows(IllegalArgumentException.class, () -> warehouseInventoryService.removeBooks(booksToRemove));
        Mockito.verify(warehouseInventoryDao, Mockito.never()).removeBooks(booksToRemove);
    }

}