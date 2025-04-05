package com.ifellow.bookstore.dao.implementations;

import com.ifellow.bookstore.dao.interfaces.StoreDao;
import com.ifellow.bookstore.datasource.DataSource;
import com.ifellow.bookstore.model.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class StoreDaoImpl implements StoreDao {

    private final DataSource dataSource;

    public StoreDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(Store store) {
        dataSource.getStores().add(store);
    }

    @Override
    public void remove(Store store) {
        dataSource.getStores().remove(store);
    }

    @Override
    public Optional<Store> findById(UUID id) {
        return dataSource.getStores().stream().filter(s -> s.getId().equals(id)).findFirst();
    }
}
