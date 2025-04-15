package com.ifellow.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "warehouse_books_amount",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"book_id", "warehouse_id"})}
)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class WarehouseBookAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer amount;
}
