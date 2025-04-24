package com.ifellow.bookstore.exceptionhandler;

import com.ifellow.bookstore.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AuthorNotFoundException.class)
    public String handleAuthorNotFoundException(AuthorNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFoundException(BookNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GenreNotFoundException.class)
    public String handleGenreNotFoundException(GenreNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(StoreNotFoundException.class)
    public String handleStoreNotFoundException(StoreNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(WarehouseNotFoundException.class)
    public String handleWarehouseNotFoundException(WarehouseNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SaleNotFoundException.class)
    public String handleSaleNotFoundException(SaleNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(OrderNotFoundException.class)
    public String handleOrderNotFoundException(OrderNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotEnoughStockException.class)
    public String handleNotEnoughStockException(NotEnoughStockException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChangeOrderStatusException.class)
    public String handleChangeOrderStatusException(ChangeOrderStatusException e) {
        return e.getMessage();
    }
}
