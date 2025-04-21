package com.ifellow.bookstore.repository;

import com.ifellow.bookstore.model.SaleItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    Page<SaleItem> findBySaleId(Long orderId, Pageable pageable);
    Page<SaleItem> findBySaleIdAndBookId(Long orderId, Long bookId, Pageable pageable);
}
