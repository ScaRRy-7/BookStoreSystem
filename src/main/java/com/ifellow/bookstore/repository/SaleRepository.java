package com.ifellow.bookstore.repository;

import com.ifellow.bookstore.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    Page<Sale> findBySaleDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Sale> findByStoreId(Long warehouseId, Pageable pageable);
    Page<Sale> findByStoreIdAndSaleDateTimeBetween(Long storeId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
