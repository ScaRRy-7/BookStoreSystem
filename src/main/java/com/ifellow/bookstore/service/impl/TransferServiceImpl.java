package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreNotFoundException;
import com.ifellow.bookstore.exception.WarehouseNotFoundException;
import com.ifellow.bookstore.service.api.StoreService;
import com.ifellow.bookstore.service.api.TransferService;
import com.ifellow.bookstore.service.api.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final StoreService storeService;
    private final WarehouseService warehouseService;

    @Override
    @Transactional
    public void transferBookFromWarehouseToStore(Long warehouseId, Long storeId, Long bookId, int quantity)
            throws WarehouseNotFoundException, StoreNotFoundException, NotEnoughStockException {

        warehouseService.removeBookFromWarehouse(warehouseId, bookId, quantity);
        storeService.addBookToStore(storeId, bookId, quantity);
    }

    @Override
    @Transactional
    public void transferBookFromStoreToStore(Long storeIdFrom, Long storeIdTo, Long bookId, int quantity)
            throws WarehouseNotFoundException, StoreNotFoundException, NotEnoughStockException {

        storeService.removeBookFromStore(storeIdFrom, bookId, quantity);
        storeService.addBookToStore(storeIdTo, bookId, quantity);
    }
}
