package com.ifellow.bookstore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "warehouses")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Warehouse {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String address;

}
