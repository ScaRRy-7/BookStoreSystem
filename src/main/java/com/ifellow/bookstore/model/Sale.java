package com.ifellow.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sales")
public class Sale {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @OneToMany(mappedBy = "sale", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<SaleItem> saleItemList = new ArrayList<>();

    @Column(name = "sale_date_time")
    private LocalDateTime saleDateTime;

    @Column
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
