package com.ifellow.bookstore.repository;

import com.ifellow.bookstore.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByAddress(String address);
}
