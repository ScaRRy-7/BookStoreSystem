package com.ifellow.bookstore;

import com.ifellow.bookstore.dto.request.*;
import com.ifellow.bookstore.dto.response.*;
import com.ifellow.bookstore.model.*;
import com.ifellow.bookstore.repository.api.AuthorRepository;
import com.ifellow.bookstore.repository.api.BookRepository;
import com.ifellow.bookstore.repository.api.GenreRepository;
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

    }
}
