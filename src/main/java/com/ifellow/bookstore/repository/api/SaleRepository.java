package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
