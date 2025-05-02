package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookBulkDto;

public interface TransferService {

    void transferBookFromWarehouseToStore(Long warehouseId, Long storeId, BookBulkDto bookBulkDto);
    void transferBookFromStoreToStore(Long warehouseId, Long storeId, BookBulkDto bookBulkDto);
}
