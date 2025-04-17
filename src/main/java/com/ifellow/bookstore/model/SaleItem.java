package com.ifellow.bookstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
@Getter
@Setter
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column
    private Integer quantity;

    @Column
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;


}
