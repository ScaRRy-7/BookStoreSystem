package com.ifellow.bookstore.datasource;

import com.ifellow.bookstore.model.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataSource {
    private static DataSource dataSource;

    @Getter
    private final List<Book> bookReserve = new ArrayList<>();
    @Getter
    private final List<Order> orders = new ArrayList<>();
    @Getter
    private final List<Sale> sales = new ArrayList<>();
    @Getter
    private final List<Receipt> receipts = new ArrayList<>();
    @Getter
    private final List<Store> stores = new ArrayList<>();
}
