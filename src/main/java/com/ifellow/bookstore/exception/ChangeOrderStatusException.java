package com.ifellow.bookstore.exception;

public class ChangeOrderStatusException extends RuntimeException {
    public ChangeOrderStatusException(String message) {
        super(message);
    }
}
