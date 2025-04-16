package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    List<SaleItem> findBySaleId(Long orderId);
    Optional<SaleItem> findBySaleIdAndBookId(Long orderId, Long bookId);
}
