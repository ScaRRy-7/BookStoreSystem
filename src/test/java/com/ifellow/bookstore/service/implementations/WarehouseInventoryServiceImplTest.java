package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.WarehouseInventoryDao;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private Book book;
    private BookRequestDto bookRequestDto;
    private BookResponseDto bookResponseDto;
    private UUID storeId;
    private final UUID WAREHOUSE_ID = null;

    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID();
        book = new Book("Мастер и Маргарита", "Михаил Булгаков", "Роман", 500, 200, WAREHOUSE_ID);
        bookRequestDto = new BookRequestDto("Мастер и Маргарита", "Михаил Булгаков", "Роман", 500, 200);
        bookResponseDto = new BookResponseDto("Мастер и Маргарита", "Михаил Булгаков", "Роман", 500, 200, WAREHOUSE_ID);

    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl производит оптовую доставку книг на склад")
    public void wholesaleBookDelivery_AddsBooks() {
        List<BookRequestDto> bookRequestDtoList = List.of(bookRequestDto);

        warehouseInventoryService.wholesaleBookDelivery(bookRequestDtoList);

        Mockito.verify(warehouseInventoryDao, Mockito.times(1)).wholesaleDelivery(Mockito.anyList());
    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl делает отчет о запасе книг")
    public void getStockReport_ReturnsStockReport() {
        Map<Book, Long> stock = Map.of(book, 10L);
        Mockito.when(warehouseInventoryDao.getBooks()).thenReturn(stock);

        Map<BookResponseDto, Long> report = warehouseInventoryService.getStockReport();

        Assertions.assertNotNull(report);
        Assertions.assertEquals(1, report.size());
        Assertions.assertEquals(10L, report.get(bookResponseDto));
    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl добавляет одну книгу")
    public void addBook_AddsSingleBook() {
        warehouseInventoryService.addBook(bookRequestDto);

        Mockito.verify(warehouseInventoryDao, Mockito.times(1)).addBook(book);
    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl удаляет одну книгу")
    public void removeBook_RemovesSingleBook() {
        warehouseInventoryService.removeBook(bookRequestDto);

        Mockito.verify(warehouseInventoryDao, Mockito.times(1)).removeBook(book);
    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl делает поиск книг по шаблону")
    public void findBooksByType_FindBooks() {
        List<Book> books = List.of(book);
        Mockito.when(warehouseInventoryDao.findBooks(book)).thenReturn(books);

        List<Book> foundBooks = warehouseInventoryService.findBooksByType(book);

        Assertions.assertNotNull(foundBooks);
        Assertions.assertEquals(1, foundBooks.size());
        Assertions.assertEquals(book, foundBooks.getFirst());
    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl удаляет список книг со склада")
    public void removeBooks_RemovesBooks() {
        List<Book> booksToRemove = List.of(book);

        warehouseInventoryService.removeBooks(booksToRemove);
        Mockito.verify(warehouseInventoryDao, Mockito.times(1)).removeBooks(booksToRemove);
    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl выбрасывает исключение при попытке удалить книги не принадлежащие складу")
    public void removeBooks_InvalidArgument_ThrowsException() {
        Book book = new Book("Неправильная книга", "Автор", "Жанр", 100, 50, storeId);
        List<Book> booksToRemove = List.of(book);

        Assertions.assertThrows(IllegalArgumentException.class, () -> warehouseInventoryService.removeBooks(booksToRemove));
        Mockito.verify(warehouseInventoryDao, Mockito.never()).removeBooks(booksToRemove);
    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl делает поиск книг по автору")
    void findBooksByAuthor_FindsBooks() {
        List<Book> booksByAuthor = List.of(book);
        Mockito.when(warehouseInventoryDao.findBooksByAuthor("Михаил Булгаков")).thenReturn(booksByAuthor);

        List<BookResponseDto> foundBooks = warehouseInventoryService.findBooksByAuthor("Михаил Булгаков");

        Assertions.assertNotNull(foundBooks);
        Assertions.assertEquals(1, foundBooks.size());
        Assertions.assertEquals(bookResponseDto, foundBooks.getFirst());
    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl делает поиск книг по названию")
    void findBooksByTitle_FindsBooks() {
        List<Book> booksByTitle = List.of(book);
        Mockito.when(warehouseInventoryDao.findBooksByTitle("Мастер и Маргарита")).thenReturn(booksByTitle);

        List<BookResponseDto> foundBooks = warehouseInventoryService.findBooksByTitle("Мастер и Маргарита");

        Assertions.assertNotNull(foundBooks);
        Assertions.assertEquals(1, foundBooks.size());
        Assertions.assertEquals(bookResponseDto, foundBooks.getFirst());
    }

    @Test
    @DisplayName("WarehouseInventoryServiceImpl группирует книги по жанру")
    void groupBooksByGenre_GroupsBooks() {
        Map<String, List<Book>> groupedBooks = Map.of("Роман", List.of(book));
        Mockito.when(warehouseInventoryDao.groupBooksByGenre()).thenReturn(groupedBooks);

        Map<String, List<BookResponseDto>> groupedDtos = warehouseInventoryService.groupBooksByGenre();

        Assertions.assertNotNull(groupedDtos);
        Assertions.assertEquals(1, groupedDtos.size());
        Assertions.assertEquals(1, groupedDtos.get("Роман").size());
        Assertions.assertEquals(bookResponseDto, groupedDtos.get("Роман").getFirst());
    }
}