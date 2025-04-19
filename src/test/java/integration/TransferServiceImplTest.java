package integration;

import com.ifellow.bookstore.dto.request.*;
import com.ifellow.bookstore.dto.response.*;
import com.ifellow.bookstore.service.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransferServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private TransferService transferService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private GenreService genreService;

    @Test
    @DisplayName("Успешно переводит книгу с склада в магазин")
    public void transferBookFromWarehouseToStore_ValidData_TransfersBooks() {
        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Варшавская");
        WarehouseResponseDto warehouseResponseDto = warehouseService.save(warehouseRequestDto);

        StoreRequestDto storeRequestDto = new StoreRequestDto("Фрунзенская");
        StoreResponseDto sRespDto = storeService.save(storeRequestDto);;

        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        AuthorResponseDto authorResponseDto = authorService.save(authorRequestDto);

        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");
        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);

        BookRequestDto bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        BookResponseDto bookResponseDto = bookService.save(bookRequestDto);

        warehouseService.addBookToWarehouse(warehouseResponseDto.id(), bookResponseDto.id(), 30);



        transferService.transferBookFromWarehouseToStore(warehouseResponseDto.id(), sRespDto.id(), bookResponseDto.id(), 25);



        assertEquals(5, warehouseService.getWarehouseStock(warehouseResponseDto.id(), PageRequest.of(0, 1)).getContent().getFirst().quantity());
        assertEquals(25, storeService.getStoreStock(sRespDto.id(), PageRequest.of(0, 1)).getContent().getFirst().quantity());
    }

    @Test
    @DisplayName("Успешно переводит книгу с магазина в магазин")
    public void transferBookFromStoreToStore_ValidData_TransfersBooks() {
        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Варшавская");
        WarehouseResponseDto warehouseResponseDto = warehouseService.save(warehouseRequestDto);

        StoreRequestDto storeRequestDto = new StoreRequestDto("Фрунзенская");
        StoreResponseDto storeResponseDto = storeService.save(storeRequestDto);
        StoreRequestDto storeRequestDto2 = new StoreRequestDto("Каширская");
        StoreResponseDto storeResponseDto2 = storeService.save(storeRequestDto2);

        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        AuthorResponseDto authorResponseDto = authorService.save(authorRequestDto);

        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");
        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);

        BookRequestDto bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        BookResponseDto bookResponseDto = bookService.save(bookRequestDto);

        warehouseService.addBookToWarehouse(warehouseResponseDto.id(), bookResponseDto.id(), 30);
        transferService.transferBookFromWarehouseToStore(warehouseResponseDto.id(), storeResponseDto.id(), bookResponseDto.id(), 25);



        transferService.transferBookFromStoreToStore(storeResponseDto.id(), storeResponseDto2.id(), bookResponseDto.id(), 15);



        assertEquals(5, warehouseService.getWarehouseStock(warehouseResponseDto.id(), PageRequest.of(0, 1)).getContent().getFirst().quantity());
        assertEquals(10, storeService.getStoreStock(storeResponseDto.id(), PageRequest.of(0, 1)).getContent().getFirst().quantity());
        assertEquals(15, storeService.getStoreStock(storeResponseDto2.id(), PageRequest.of(0, 1)).getContent().getFirst().quantity());

    }
}