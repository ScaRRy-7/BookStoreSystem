package unit.service;

import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreBookResponseDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.exception.BookException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreException;
import com.ifellow.bookstore.mapper.StoreBookAmountMapper;
import com.ifellow.bookstore.mapper.StoreMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.model.StoreBookAmount;
import com.ifellow.bookstore.repository.StoreBookAmountRepository;
import com.ifellow.bookstore.repository.StoreRepository;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.impl.StoreServiceImpl;
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
class StoreServiceImplTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreMapper storeMapper;

    @Mock
    private BookService bookService;

    @Mock
    private StoreBookAmountRepository storeBookAmountRepository;

    @Mock
    private StoreBookAmountMapper storeBookAmountMapper;

    @InjectMocks
    private StoreServiceImpl storeService;

    private Store store;
    private Book book;
    private StoreRequestDto storeRequestDto;
    private Long storeId;
    private Long bookId;
    private int quantity;

    @BeforeEach
    void setUp() {
        storeId = 1L;
        bookId = 1L;
        quantity = 10;

        store = new Store();
        store.setId(storeId);
        store.setAddress("Тестовый адрес");

        book = new Book();
        book.setId(bookId);

        storeRequestDto = new StoreRequestDto("Тестовый адрес");
    }

    @Test
    @DisplayName("Успешно сохраняет новый магазин с валидными данными")
    void save_ValidData_SavesStore() {
        StoreResponseDto responseDto = new StoreResponseDto(storeId, "Тестовый адрес");

        Mockito.when(storeMapper.toEntity(storeRequestDto)).thenReturn(store);
        Mockito.when(storeRepository.save(store)).thenReturn(store);
        Mockito.when(storeMapper.toDto(store)).thenReturn(responseDto);

        StoreResponseDto result = storeService.save(storeRequestDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        Mockito.verify(storeMapper).toEntity(storeRequestDto);
        Mockito.verify(storeRepository).save(store);
        Mockito.verify(storeMapper).toDto(store);
    }

    @Test
    @DisplayName("Находит магазин по id")
    void findStoreById_ExistingId_ReturnsStore() {
        Mockito.when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        Store result = storeService.findStoreById(storeId);

        assertNotNull(result);
        assertEquals(store, result);
        Mockito.verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("выбрасывает StoreNotFoundException когда магазин не найден по id")
    void findStoreById_NotExistingId_ThrowsException() {
        Mockito.when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThrows(StoreException.class, () -> storeService.findStoreById(storeId));
        Mockito.verify(storeRepository).findById(storeId);
    }

    @Test
    @DisplayName("Успешно добавляет книгу в магазин в количестве quantity")
    void addBookToStore_NewBook_AddsBook() {
        StoreBookAmount sba = new StoreBookAmount();
        sba.setStore(store);
        sba.setBook(book);
        sba.setAmount(quantity);

        Mockito.when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        Mockito.when(bookService.findBookById(bookId)).thenReturn(book);
        Mockito.when(storeBookAmountRepository.findByStoreIdAndBookId(storeId, bookId)).thenReturn(Optional.empty());
        Mockito.when(storeBookAmountRepository.save(Mockito.any(StoreBookAmount.class))).thenReturn(sba);

        storeService.addBookToStore(storeId, bookId, quantity);

        Mockito.verify(storeRepository).findById(storeId);
        Mockito.verify(bookService).findBookById(bookId);
        Mockito.verify(storeBookAmountRepository).findByStoreIdAndBookId(storeId, bookId);
        Mockito.verify(storeBookAmountRepository).save(Mockito.any(StoreBookAmount.class));
    }

    @Test
    @DisplayName("Обновляет количество книги в магазине")
    void addBookToStore_ExistingBook_UpdatesQuantity() {
        int initialAmount = 5;
        StoreBookAmount sba = new StoreBookAmount();
        sba.setStore(store);
        sba.setBook(book);
        sba.setAmount(initialAmount);

        Mockito.when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        Mockito.when(bookService.findBookById(bookId)).thenReturn(book);
        Mockito.when(storeBookAmountRepository.findByStoreIdAndBookId(storeId, bookId)).thenReturn(Optional.of(sba));
        Mockito.when(storeBookAmountRepository.save(sba)).thenReturn(sba);

        storeService.addBookToStore(storeId, bookId, quantity);

        assertEquals(initialAmount + quantity, sba.getAmount());
        Mockito.verify(storeBookAmountRepository).save(sba);
    }

    @Test
    @DisplayName("Выбрасывает IllegalArgumentException когда quantity <= 0")
    void addBookToStore_InvalidQuantity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> storeService.addBookToStore(storeId, bookId, 0));
        assertThrows(IllegalArgumentException.class, () -> storeService.addBookToStore(storeId, bookId, -1));
        Mockito.verify(storeRepository, Mockito.never()).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("Удаляет книгу с магазина")
    void removeBookFromStore_ValidData_RemovesBook() {
        int initialAmount = 10;
        int quantityToRemove = 5;
        StoreBookAmount sba = new StoreBookAmount();
        sba.setStore(store);
        sba.setBook(book);
        sba.setAmount(initialAmount);

        Mockito.when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        Mockito.when(storeBookAmountRepository.findByStoreIdAndBookId(storeId, bookId)).thenReturn(Optional.of(sba));
        Mockito.when(storeBookAmountRepository.save(sba)).thenReturn(sba);

        storeService.removeBookFromStore(storeId, bookId, quantityToRemove);

        assertEquals(initialAmount - quantityToRemove, sba.getAmount());
        Mockito.verify(storeBookAmountRepository).save(sba);
    }

    @Test
    @DisplayName("Выбрасывает NotEnoughStockException когда происходит попытка удалить большее кол-во книги чем есть в магазине")
    void removeBookFromStore_NotEnoughStock_ThrowsException() {
        StoreBookAmount sba = new StoreBookAmount();
        sba.setAmount(5);

        Mockito.when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        Mockito.when(storeBookAmountRepository.findByStoreIdAndBookId(storeId, bookId)).thenReturn(Optional.of(sba));

        assertThrows(NotEnoughStockException.class, () -> storeService.removeBookFromStore(storeId, bookId, 10));
        Mockito.verify(storeBookAmountRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Выбрасывает BookNotFoundException когда книга не найдена в магазине")
    void removeBookFromStore_BookNotFound_ThrowsException() {
        Mockito.when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        Mockito.when(storeBookAmountRepository.findByStoreIdAndBookId(storeId, bookId)).thenReturn(Optional.empty());

        assertThrows(BookException.class, () -> storeService.removeBookFromStore(storeId, bookId, quantity));
        Mockito.verify(storeBookAmountRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Возвращает инвентарь магазина")
    void getStoreStock_ValidId_ReturnsStock() {
        Pageable pageable = Pageable.unpaged();
        StoreBookAmount sba = new StoreBookAmount();
        sba.setId(1L);
        sba.setAmount(10);
        sba.setStore(store);
        sba.setBook(book);
        Page<StoreBookAmount> page = new PageImpl<>(List.of(sba));
        StoreBookResponseDto responseDto = new StoreBookResponseDto(1L, bookId, storeId, 10);

        Mockito.when(storeBookAmountRepository.findByStoreId(storeId, pageable)).thenReturn(page);
        Mockito.when(storeBookAmountMapper.toDto(sba)).thenReturn(responseDto);

        Page<StoreBookResponseDto> result = storeService.getStoreStock(storeId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDto, result.getContent().get(0));
        Mockito.verify(storeBookAmountRepository).findByStoreId(storeId, pageable);
        Mockito.verify(storeBookAmountMapper).toDto(sba);
    }
}