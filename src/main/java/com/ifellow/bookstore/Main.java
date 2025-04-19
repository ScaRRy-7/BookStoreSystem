package com.ifellow.bookstore;

import com.ifellow.bookstore.dto.request.*;
import com.ifellow.bookstore.dto.response.*;
import com.ifellow.bookstore.service.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        AuthorService authorService = context.getBean(AuthorService.class);
        GenreService genreService = context.getBean(GenreService.class);
        BookService bookService = context.getBean(BookService.class);
        WarehouseService warehouseService = context.getBean(WarehouseService.class);
        StoreService storeService = context.getBean(StoreService.class);
        TransferService transferService = context.getBean(TransferService.class);
        SaleService saleService = context.getBean(SaleService.class);
        OrderService orderService = context.getBean(OrderService.class);

        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");
        GenreRequestDto genreRequestDto2 = new GenreRequestDto("Повесть");
        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);
        GenreResponseDto genreResponseDto2 = genreService.save(genreRequestDto2);

        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        AuthorResponseDto authorResponseDto = authorService.save(authorRequestDto);

        BookRequestDto bookRequestDto = new BookRequestDto("Преступление и наказание",
                authorResponseDto.id(), genreResponseDto.id(), new BigDecimal("128.00"));
        BookResponseDto bookResponseDto = bookService.save(bookRequestDto);

        BookRequestDto bookRequestDto2 = new BookRequestDto("ЕЩе одна книга",
                authorResponseDto.id(), genreResponseDto2.id(), new BigDecimal("256.00"));
        BookResponseDto bookResponseDto2 = bookService.save(bookRequestDto2);

        System.out.println("----");
        Page<BookResponseDto> bookResponseDtoPage = bookService.findAllGroupedByGenre(PageRequest.of(0, 3));

            bookResponseDtoPage.getContent().forEach(System.out::println);

        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Ул. Арбат");
        WarehouseResponseDto warehouseResponseDto = warehouseService.save(warehouseRequestDto);

        List<BookBulkAddDto> booksToAdd = new ArrayList<>();
        booksToAdd.add(new BookBulkAddDto(bookResponseDto.id(), 1000));
        booksToAdd.add(new BookBulkAddDto(bookResponseDto2.id(), 300));
        warehouseService.bulkAddBooks(warehouseResponseDto.id(), booksToAdd);

        Page<WarehouseBookResponseDto> warehouseBookResponseDtos = warehouseService.getWarehouseStock(warehouseResponseDto.id(), PageRequest.of(0, 5));


        StoreRequestDto storeRequestDto = new StoreRequestDto("Ул. Лузана");
        StoreResponseDto storeResponseDto = storeService.save(storeRequestDto);

        transferService.transferBookFromWarehouseToStore(warehouseResponseDto.id(), storeResponseDto.id(), bookResponseDto.id(), 50);
        transferService.transferBookFromWarehouseToStore(warehouseResponseDto.id(), storeResponseDto.id(), bookResponseDto2.id(), 300);

        List<BookSaleDto> bookSaleDtos = new ArrayList<>();
        bookSaleDtos.add(new BookSaleDto(bookResponseDto.id(), 50));
        bookSaleDtos.add(new BookSaleDto(bookResponseDto2.id(), 20));
        System.out.println("------------------------------------------------------");
        SaleResponseDto saleResponseDto = saleService.processSale(storeResponseDto.id(), bookSaleDtos);
        transferService.transferBookFromWarehouseToStore(warehouseResponseDto.id(), storeResponseDto.id(), bookResponseDto.id(), 30);




        List<BookOrderDto> bookOrderDtos = new ArrayList<>();
        bookOrderDtos.add(new BookOrderDto(bookResponseDto.id(), 49));
        OrderResponseDto  orderResponseDto = orderService.create(warehouseResponseDto.id(), bookOrderDtos);
        orderService.cancelById(1L);

    }
}
