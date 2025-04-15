package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
