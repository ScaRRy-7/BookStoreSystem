package com.ifellow.bookstore;

import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.service.interfaces.StoreService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    }
}
