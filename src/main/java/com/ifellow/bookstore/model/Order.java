package com.ifellow.bookstore.model;

import com.ifellow.bookstore.enumeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Order {
    private final UUID id;
    private final UUID storeId;
    private final List<Book> books;
    private final Date createdDate;
    @Setter
    @Getter
    private OrderStatus status;
    private final double totalAmount;

    public Order(UUID storeId, List<Book> books, double totalAmount) {
        this.id = UUID.randomUUID();
        this.storeId = storeId;
        this.books = List.copyOf(books);
        this.createdDate = new Date();
        this.status = OrderStatus.CREATED;
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", storeId=" + storeId +
                ", books=" + books +
                ", createdDate=" + createdDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
