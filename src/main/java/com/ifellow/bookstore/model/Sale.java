package com.ifellow.bookstore.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Getter
@Setter
public class Sale {
    private UUID id;
    private UUID storeId;
    private List<Book> books;
    private Date saleDate;

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", storeId=" + storeId +
                ", books=" + books +
                ", saleDate=" + saleDate +
                '}';
    }
}
