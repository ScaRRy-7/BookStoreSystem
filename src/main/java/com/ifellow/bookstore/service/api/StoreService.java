package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreBookResponseDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {

    StoreResponseDto save(StoreRequestDto storeRequestDto);
    Store findStoreById(Long id);
    void addBookToStore(Long id, Long bookId, int quantity);
    void removeBookFromStore(Long id, Long bookId, int quantity);
    Page<StoreBookResponseDto> getStoreStock(Long id, Pageable pageable);
}
