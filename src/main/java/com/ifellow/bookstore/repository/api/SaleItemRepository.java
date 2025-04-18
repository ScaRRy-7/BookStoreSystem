package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.SaleItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    Page<SaleItem> findBySaleId(Long orderId, Pageable pageable);
    Page<SaleItem> findBySaleIdAndBookId(Long orderId, Long bookId, Pageable pageable);
}
