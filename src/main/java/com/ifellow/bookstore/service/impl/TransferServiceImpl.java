package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.service.api.StoreService;
import com.ifellow.bookstore.service.api.TransferService;
import com.ifellow.bookstore.service.api.WarehouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferServiceImpl implements TransferService {

    private final StoreService storeService;
    private final WarehouseService warehouseService;

    public TransferServiceImpl(StoreService storeService, WarehouseService warehouseService) {
        this.storeService = storeService;
        this.warehouseService = warehouseService;
    }

    @Override
    @Transactional
    public void transferBookFromWarehouseToStore(Long warehouseId, Long storeId, Long bookId, int quantity) {
        warehouseService.removeBookFromWarehouse(warehouseId, bookId, quantity);
        storeService.addBookToStore(storeId, bookId, quantity);
    }

    @Override
    @Transactional
    public void transferBookFromStoreToStore(Long storeIdFrom, Long storeIdTo, Long bookId, int quantity) {
        storeService.removeBookFromStore(storeIdFrom, bookId, quantity);
        storeService.addBookToStore(storeIdTo, bookId, quantity);
    }
}
