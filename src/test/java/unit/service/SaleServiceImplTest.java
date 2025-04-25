package unit.service;

import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.mapper.SaleMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Sale;
import com.ifellow.bookstore.model.SaleItem;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.repository.SaleItemRepository;
import com.ifellow.bookstore.repository.SaleRepository;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.StoreService;
import com.ifellow.bookstore.service.impl.SaleServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleItemRepository saleItemRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private BookService bookService;

    @Mock
    private SaleMapper saleMapper;

    @InjectMocks
    private SaleServiceImpl saleService;

    private Long storeId;
    private Long bookId;
    private int quantity;
    private BookSaleDto bookSaleDto;
    private Store store;
    private Book book;
    private Sale sale;
    private SaleItem saleItem;
    private SaleResponseDto saleResponseDto;
    private LocalDateTime saleTime;

    @BeforeEach
    void setUp() {
        storeId = 1L;
        bookId = 1L;
        quantity = 2;
        bookSaleDto = new BookSaleDto(bookId, quantity);
        saleTime = LocalDateTime.now();

        store = new Store();
        store.setId(storeId);

        book = new Book();
        book.setId(bookId);
        book.setPrice(BigDecimal.valueOf(100));

        sale = new Sale();
        sale.setId(1L);
        sale.setStore(store);
        sale.setSaleDateTime(saleTime);

        saleItem = new SaleItem();
        saleItem.setBook(book);
        saleItem.setQuantity(quantity);
        saleItem.setPrice(book.getPrice());
        saleItem.setSale(sale);

        sale.setSaleItemList(List.of(saleItem));
        sale.setTotalPrice(BigDecimal.valueOf(200));

        saleResponseDto = new SaleResponseDto(sale.getId(), saleTime, storeId, sale.getTotalPrice());
    }

    @Test
    @DisplayName("Успешная продажа книг")
    void processSale_ValidData_ProcessesSale() {
        Mockito.when(storeService.findStoreById(storeId)).thenReturn(store);
        Mockito.when(bookService.findBookById(bookId)).thenReturn(book);
        Mockito.when(saleRepository.save(Mockito.any(Sale.class))).thenReturn(sale);
        Mockito.when(saleMapper.toDto(Mockito.any(Sale.class))).thenReturn(saleResponseDto);

        SaleResponseDto result = saleService.processSale(storeId, List.of(bookSaleDto));

        assertNotNull(result);
        assertEquals(saleResponseDto, result);
        Mockito.verify(storeService).removeBookFromStore(storeId, bookId, quantity);
        Mockito.verify(saleRepository).save(Mockito.any(Sale.class));
    }


    @Test
    @DisplayName("Получение продажи по ID")
    void findById_ExistingId_ReturnsSale() {
        Long saleId = 1L;
        Mockito.when(saleRepository.findById(saleId)).thenReturn(Optional.of(sale));
        Mockito.when(saleMapper.toDto(sale)).thenReturn(saleResponseDto);

        SaleResponseDto result = saleService.findById(saleId);

        assertNotNull(result);
        assertEquals(saleResponseDto, result);
        Mockito.verify(saleRepository).findById(saleId);
    }

    @Test
    @DisplayName("Исключение при недостатке книг на складе")
    void processSale_NotEnoughStock_ThrowsException() {
        Mockito.when(storeService.findStoreById(storeId)).thenReturn(store);
        Mockito.doThrow(new NotEnoughStockException("Not enough stock")).when(storeService).removeBookFromStore(storeId, bookId, quantity);

        assertThrows(NotEnoughStockException.class, () -> saleService.processSale(storeId, List.of(bookSaleDto)));
        Mockito.verify(storeService).removeBookFromStore(storeId, bookId, quantity);
        Mockito.verify(saleRepository, Mockito.never()).save(Mockito.any(Sale.class));
    }
}