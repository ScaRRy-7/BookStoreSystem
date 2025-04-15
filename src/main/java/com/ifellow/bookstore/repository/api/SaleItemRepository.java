package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
}
