package com.ifellow.bookstore.dao.interfaces;

import com.ifellow.bookstore.model.Store;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreDao {
    void add(Store store);
    void remove(Store store);
    Optional<Store> findById(UUID id);
}
