package unit.service;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.dto.response.WarehouseResponseDto;
import com.ifellow.bookstore.exception.BookNotFoundException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.WarehouseNotFoundException;
import com.ifellow.bookstore.mapper.WarehouseBookAmountMapper;
import com.ifellow.bookstore.mapper.WarehouseMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Warehouse;
import com.ifellow.bookstore.model.WarehouseBookAmount;
import com.ifellow.bookstore.repository.WarehouseBookAmountRepository;
import com.ifellow.bookstore.repository.WarehouseRepository;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.impl.WarehouseServiceImpl;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseBookAmountRepository warehouseBookAmountRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @Mock
    private BookService bookService;

    @Mock
    private WarehouseBookAmountMapper warehouseBookAmountMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private Warehouse warehouse;
    private Book book;
    private WarehouseRequestDto warehouseRequestDto;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setAddress("Склад 1");

        book = new Book();
        book.setId(1L);

        warehouseRequestDto = new WarehouseRequestDto("Склад 1");
    }

    @Test
    @DisplayName("WarehouseServiceImpl успешно сохраняет склад")
    void save_ValidData_SaveWarehouse() {
        WarehouseResponseDto responseDto = new WarehouseResponseDto(1L, "Склад 1");
        Mockito.when(warehouseMapper.toEntity(warehouseRequestDto)).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        Mockito.when(warehouseMapper.toDto(warehouse)).thenReturn(responseDto);

        WarehouseResponseDto result = warehouseService.save(warehouseRequestDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        Mockito.verify(warehouseMapper, Mockito.times(1)).toEntity(warehouseRequestDto);
        Mockito.verify(warehouseRepository, Mockito.times(1)).save(warehouse);
        Mockito.verify(warehouseMapper, Mockito.times(1)).toDto(warehouse);
    }

    @Test
    @DisplayName("WarehouseServiceImpl находит склад по id")
    void findWarehouseById_ExistingId_ReturnWarehouse() {
        Mockito.when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        Warehouse result = warehouseService.findWarehouseById(1L);

        assertNotNull(result);
        assertEquals(warehouse, result);
        Mockito.verify(warehouseRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("WarehouseServiceImpl выбрасывает исключение, если склад не найден")
    void findWarehouseById_NotExistingId_ThrowsException() {
        Mockito.when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.findWarehouseById(1L));
        Mockito.verify(warehouseRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("WarehouseServiceImpl успешно добавляет новую книгу на склад")
    void addBookToWarehouse_NewBook_AddBook() {
        int quantity = 10;
        WarehouseBookAmount wba = new WarehouseBookAmount();
        wba.setWarehouse(warehouse);
        wba.setBook(book);
        wba.setAmount(quantity);
        Mockito.when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        Mockito.when(bookService.findBookById(1L)).thenReturn(book);
        Mockito.when(warehouseBookAmountRepository.findByWarehouseIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        Mockito.when(warehouseBookAmountRepository.save(Mockito.any(WarehouseBookAmount.class))).thenReturn(wba);

        warehouseService.addBookToWarehouse(1L, 1L, quantity);

        Mockito.verify(warehouseRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(bookService, Mockito.times(1)).findBookById(1L);
        Mockito.verify(warehouseBookAmountRepository, Mockito.times(1)).findByWarehouseIdAndBookId(1L, 1L);
        Mockito.verify(warehouseBookAmountRepository, Mockito.times(1)).save(Mockito.any(WarehouseBookAmount.class));
    }

    @Test
    @DisplayName("WarehouseServiceImpl выбрасывает исключение, если quantity <= 0")
    void addBookToWarehouse_InvalidQuantity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> warehouseService.addBookToWarehouse(1L, 1L, 0));
        Mockito.verify(warehouseRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("WarehouseServiceImpl успешно удаляет книги со склада")
    void removeBookFromWarehouse_ValidData_RemoveBook() {
        int initialAmount = 10;
        int quantityToRemove = 5;
        WarehouseBookAmount wba = new WarehouseBookAmount();
        wba.setWarehouse(warehouse);
        wba.setBook(book);
        wba.setAmount(initialAmount);
        Mockito.when(warehouseRepository.existsById(1L)).thenReturn(true);
        Mockito.when(warehouseBookAmountRepository.findByWarehouseIdAndBookId(1L, 1L)).thenReturn(Optional.of(wba));

        warehouseService.removeBookFromWarehouse(1L, 1L, quantityToRemove);

        assertEquals(initialAmount - quantityToRemove, wba.getAmount());
        Mockito.verify(warehouseBookAmountRepository, Mockito.times(1)).save(wba);
    }

    @Test
    @DisplayName("WarehouseServiceImpl выбрасывает исключение, если недостаточно книг")
    void removeBookFromWarehouse_NotEnoughStock_ThrowsException() {
        WarehouseBookAmount wba = new WarehouseBookAmount();
        wba.setAmount(5);
        Mockito.when(warehouseRepository.existsById(1L)).thenReturn(true);
        Mockito.when(warehouseBookAmountRepository.findByWarehouseIdAndBookId(1L, 1L)).thenReturn(Optional.of(wba));

        assertThrows(NotEnoughStockException.class, () -> warehouseService.removeBookFromWarehouse(1L, 1L, 10));
    }

    @Test
    @DisplayName("WarehouseServiceImpl выбрасывает исключение, если книга не найдена на складе")
    void removeBookFromWarehouse_BookNotFound_ThrowsException() {
        Mockito.when(warehouseRepository.existsById(1L)).thenReturn(true);
        Mockito.when(warehouseBookAmountRepository.findByWarehouseIdAndBookId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> warehouseService.removeBookFromWarehouse(1L, 1L, 5));
    }

    @Test
    @DisplayName("WarehouseServiceImpl возвращает запасы склада")
    void getWarehouseStock_ValidId_ReturnStock() {
        Pageable pageable = Pageable.unpaged();
        WarehouseBookAmount wba = new WarehouseBookAmount();
        wba.setId(1L);
        wba.setAmount(10);
        wba.setWarehouse(warehouse);
        wba.setBook(book);
        Page<WarehouseBookAmount> page = new PageImpl<>(List.of(wba));
        WarehouseBookResponseDto responseDto = new WarehouseBookResponseDto(1L, 1L, 1L, 10);
        Mockito.when(warehouseBookAmountRepository.findByWarehouseId(1L, pageable)).thenReturn(page);
        Mockito.when(warehouseBookAmountMapper.toDto(wba)).thenReturn(responseDto);

        Page<WarehouseBookResponseDto> result = warehouseService.getWarehouseStock(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().get(0));
        Mockito.verify(warehouseBookAmountRepository, Mockito.times(1)).findByWarehouseId(1L, pageable);
    }

    @Test
    @DisplayName("WarehouseServiceImpl успешно добавляет несколько книг")
    void bulkAddBooks_ValidData_AddBooks() {
        List<BookBulkDto> booksToAdd = List.of(
                new BookBulkDto(1L, 5),
                new BookBulkDto(2L, 10)
        );
        Mockito.when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        Mockito.when(bookService.findBookById(1L)).thenReturn(book);
        Mockito.when(warehouseBookAmountRepository.findByWarehouseIdAndBookId(1L, 1L)).thenReturn(Optional.empty());
        Mockito.when(warehouseBookAmountRepository.save(Mockito.any(WarehouseBookAmount.class))).thenReturn(new WarehouseBookAmount());
        Mockito.when(bookService.findBookById(2L)).thenReturn(new Book());
        Mockito.when(warehouseBookAmountRepository.findByWarehouseIdAndBookId(1L, 2L)).thenReturn(Optional.empty());

        warehouseService.bulkAddBooks(1L, booksToAdd);

        Mockito.verify(warehouseBookAmountRepository, Mockito.times(2)).save(Mockito.any(WarehouseBookAmount.class));
        Mockito.verify(warehouseRepository, Mockito.times(2)).findById(1L);
        Mockito.verify(bookService, Mockito.times(1)).findBookById(1L);
        Mockito.verify(bookService, Mockito.times(1)).findBookById(2L);
    }
}