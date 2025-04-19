package com.ifellow.bookstore.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.PERSIST, orphanRemoval = true) // cascade = CascadeType.ALL
    private List<SaleItem> saleItemList = new ArrayList<>();

    @Column(name = "sale_date_time")
    private LocalDateTime saleDateTime;

    @Column
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
