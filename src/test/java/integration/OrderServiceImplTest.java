package integration;

import com.ifellow.bookstore.dto.request.*;
import com.ifellow.bookstore.dto.response.*;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.exception.ChangeOrderStatusException;
import com.ifellow.bookstore.model.OrderItem;
import com.ifellow.bookstore.repository.api.OrderItemRepository;
import com.ifellow.bookstore.service.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceImplTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private BookService bookService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private GenreService genreService;;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("Успешно создает заказ и возвращает OrderResponseDto")
    public void create_ValidBookOrderDtoList_ReturnsOrderResponseDto() {
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        AuthorResponseDto authorResponseDto = authorService.save(authorRequestDto);

        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");
        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);

        BookRequestDto bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        BookResponseDto bookResponseDto = bookService.save(bookRequestDto);

        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Ул. Арбат");
        WarehouseResponseDto warehouseResponseDto = warehouseService.save(warehouseRequestDto);
        warehouseService.addBookToWarehouse(warehouseResponseDto.id(), bookResponseDto.id(), 20);

        List<BookOrderDto> bookOrderDtoList= new ArrayList<>();
        bookOrderDtoList.add(new BookOrderDto(bookResponseDto.id(), 20));



        OrderResponseDto orderResponseDto = orderService.create(warehouseResponseDto.id(), bookOrderDtoList);
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderResponseDto.id());



        assertNotNull(orderResponseDto);
        assertEquals(OrderStatus.CREATED, orderResponseDto.orderStatus());
        assertEquals(bookOrderDtoList.getFirst().quantity(), orderItemList.getFirst().getQuantity());
        assertEquals(bookOrderDtoList.getFirst().bookId(), orderItemList.getFirst().getBook().getId());
    }

    @Test
    @DisplayName("Успешно выполняет заказ по id и возвращает OrderResponseDto")
    public void completeById_ValidId_ReturnsOrderResponseDto() {
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        AuthorResponseDto authorResponseDto = authorService.save(authorRequestDto);

        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");
        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);

        BookRequestDto bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        BookResponseDto bookResponseDto = bookService.save(bookRequestDto);

        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Ул. Арбат");
        WarehouseResponseDto warehouseResponseDto = warehouseService.save(warehouseRequestDto);
        warehouseService.addBookToWarehouse(warehouseResponseDto.id(), bookResponseDto.id(), 20);

        List<BookOrderDto> bookOrderDtoList= new ArrayList<>();
        bookOrderDtoList.add(new BookOrderDto(bookResponseDto.id(), 20));



        OrderResponseDto orderCreatedDto = orderService.create(warehouseResponseDto.id(), bookOrderDtoList);
        OrderResponseDto orderCompletedDto = orderService.completeById(orderCreatedDto.id());



        assertNotNull(orderCompletedDto);
        assertEquals( OrderStatus.COMPLETED, orderCompletedDto.orderStatus());
        assertEquals(0, warehouseService.getWarehouseStock(warehouseResponseDto.id(), PageRequest.of(0, 1)).getContent().getFirst().quantity());
    }

    @Test
    @DisplayName("Успешно отменяет заказ, возвращает OrderResponseDto и возвращает книги на склад")
    public void cancelById_ValidId_ReturnsOrderResponseDtoAndReturnsBooksToWarehouse() {
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        AuthorResponseDto authorResponseDto = authorService.save(authorRequestDto);

        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");
        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);

        BookRequestDto bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        BookResponseDto bookResponseDto = bookService.save(bookRequestDto);

        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Ул. Арбат");
        WarehouseResponseDto warehouseResponseDto = warehouseService.save(warehouseRequestDto);
        warehouseService.addBookToWarehouse(warehouseResponseDto.id(), bookResponseDto.id(), 20);

        List<BookOrderDto> bookOrderDtoList= new ArrayList<>();
        bookOrderDtoList.add(new BookOrderDto(bookResponseDto.id(), 20));



        OrderResponseDto orderCreatedDto = orderService.create(warehouseResponseDto.id(), bookOrderDtoList);
        OrderResponseDto orderCanceledDto = orderService.cancelById(orderCreatedDto.id());



        assertNotNull(orderCanceledDto);
        assertEquals(OrderStatus.CANCELED, orderCanceledDto.orderStatus());
        assertEquals(20, warehouseService.getWarehouseStock(warehouseResponseDto.id(), PageRequest.of(0, 1)).getContent().getFirst().quantity());
    }

    @Test
    @DisplayName("Выбрасывает ошибку при попытке отменить заказ, который уже выполнен/отменен")
    public void cancelById_InvalidOrderStatus_ChangeOrderStatusException() {
        AuthorRequestDto authorRequestDto = new AuthorRequestDto("Федор Достоевский");
        AuthorResponseDto authorResponseDto = authorService.save(authorRequestDto);

        GenreRequestDto genreRequestDto = new GenreRequestDto("Роман");
        GenreResponseDto genreResponseDto = genreService.save(genreRequestDto);

        BookRequestDto bookRequestDto = new BookRequestDto("Преступление и наказание", authorResponseDto.id(), genreResponseDto.id(), BigDecimal.valueOf(250L));
        BookResponseDto bookResponseDto = bookService.save(bookRequestDto);

        WarehouseRequestDto warehouseRequestDto = new WarehouseRequestDto("Ул. Арбат");
        WarehouseResponseDto warehouseResponseDto = warehouseService.save(warehouseRequestDto);
        warehouseService.addBookToWarehouse(warehouseResponseDto.id(), bookResponseDto.id(), 20);

        List<BookOrderDto> bookOrderDtoList= new ArrayList<>();
        bookOrderDtoList.add(new BookOrderDto(bookResponseDto.id(), 20));



        OrderResponseDto orderCreatedDto = orderService.create(warehouseResponseDto.id(), bookOrderDtoList);
        OrderResponseDto orderCanceledDto = orderService.cancelById(orderCreatedDto.id());



        assertThrows(ChangeOrderStatusException.class, () -> orderService.cancelById(orderCanceledDto.id()));

    }
}
