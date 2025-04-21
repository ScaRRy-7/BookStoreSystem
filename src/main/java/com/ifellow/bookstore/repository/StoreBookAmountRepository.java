package com.ifellow.bookstore.repository;

import com.ifellow.bookstore.model.StoreBookAmount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreBookAmountRepository extends JpaRepository<StoreBookAmount, Long> {

    Page<StoreBookAmount> findByStoreId(Long storeId, Pageable pageable);
    Optional<StoreBookAmount> findByStoreIdAndBookId(Long storeId, Long bookId);
    boolean existsByStoreIdAndBookId(Long storeId, Long bookId);
}
