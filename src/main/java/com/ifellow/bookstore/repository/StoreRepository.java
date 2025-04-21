package com.ifellow.bookstore.repository;

import com.ifellow.bookstore.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByAddress(String address);
}
