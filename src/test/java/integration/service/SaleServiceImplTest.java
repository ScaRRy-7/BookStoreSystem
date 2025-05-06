package integration.service;

import com.ifellow.bookstore.dto.request.*;
import com.ifellow.bookstore.dto.response.*;
import com.ifellow.bookstore.service.api.*;
import integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private SaleService saleService;
    @Autowired
    private BookService bookService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private GenreService genreService;;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        UserDetails userDetails = new User(
                "client", "", List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT"),
                new SimpleGrantedAuthority("ROLE_MANAGER"),
                new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Производит успешную покупку книг в магазине")
    public void processSale_ValidData_ProcessesSale() {
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        AuthorResponseDto authorResponseDto = authorService.save(authorRequestDto);

        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");
        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);

        BookRequestDto bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        BookResponseDto bookResponseDto = bookService.save(bookRequestDto);

        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Ул. Арбат");
        WarehouseResponseDto warehouseResponseDto = warehouseService.save(warehouseRequestDto);
        warehouseService.addBookToWarehouse(warehouseResponseDto.id(), new BookBulkDto(bookResponseDto.id(), 20));

        StoreRequestDto storeRequestDto = new StoreRequestDto("Фрунзенская");
        StoreResponseDto storeResponseDto = storeService.save(storeRequestDto);

        transferService
                .transferBookFromWarehouseToStore(warehouseResponseDto.id(), storeResponseDto.id(), new BookBulkDto(bookResponseDto.id(), 20));

        List<BookSaleDto> bookSaleDtoList = new ArrayList<>();
        bookSaleDtoList.add(new BookSaleDto(bookResponseDto.id(), 20));



        SaleResponseDto saleResponseDto = saleService.processSale(storeResponseDto.id(), bookSaleDtoList);



        assertNotNull(saleResponseDto);
        assertEquals(BigDecimal.valueOf(bookSaleDtoList.getFirst().quantity()).multiply(bookResponseDto.price()), saleResponseDto.totalPrice());
    }

}