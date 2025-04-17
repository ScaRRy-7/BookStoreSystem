package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.WarehouseBookAmount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseBookAmountRepository extends JpaRepository<WarehouseBookAmount, Long> {

    Page<WarehouseBookAmount> findByWarehouseId(Long warehouseId, Pageable pageable);
    Optional<WarehouseBookAmount> findByWarehouseIdAndBookId(Long warehouseId, Long bookId);
    boolean existsByWarehouseIdAndBookId(Long warehouseId, Long bookId);
}
