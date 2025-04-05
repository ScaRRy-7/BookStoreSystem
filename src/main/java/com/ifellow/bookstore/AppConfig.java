package com.ifellow.bookstore;

import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.service.interfaces.StoreService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@ComponentScan(basePackages = "com.ifellow.bookstore")
public class AppConfig {

}
