package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
