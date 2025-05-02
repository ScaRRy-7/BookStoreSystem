package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreException;
import com.ifellow.bookstore.exception.WarehouseException;
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
    public void transferBookFromWarehouseToStore(Long warehouseId, Long storeId, BookBulkDto bookBulkDto)
            throws WarehouseException, StoreException, NotEnoughStockException {

        warehouseService.removeBookFromWarehouse(warehouseId, bookBulkDto);
        storeService.addBookToStore(storeId, bookBulkDto);
    }

    @Override
    @Transactional
    public void transferBookFromStoreToStore(Long storeIdFrom, Long storeIdTo, BookBulkDto bookBulkDto)
            throws WarehouseException, StoreException, NotEnoughStockException {

        storeService.removeBookFromStore(storeIdFrom, bookBulkDto);
        storeService.addBookToStore(storeIdTo, bookBulkDto);
    }
}
