package com.ifellow.bookstore.service.api;

public interface TransferService {

    void transferBookFromWarehouseToStore(Long warehouseId, Long storeId, Long bookId, int quantity);
    void transferBookFromStoreToStore(Long storeIdFrom, Long storeIdTo, Long bookId, int quantity);
}
