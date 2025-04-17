package com.ifellow.bookstore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter
@Setter
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true) // cascade = CascadeType.ALL
    private List<SaleItem> saleItemList = new ArrayList<>();

    @Column(name = "sale_date_time")
    private LocalDateTime saleDateTime;

    @Column
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
