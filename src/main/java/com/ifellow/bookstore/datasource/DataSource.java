package com.ifellow.bookstore.datasource;

import com.ifellow.bookstore.model.*;
import lombok.Getter;

import java.util.*;

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


    private DataSource() {}

    public static synchronized DataSource getInstance() {
        if (dataSource == null) {
            dataSource = new DataSource();
        }
        return dataSource;
    }
}
