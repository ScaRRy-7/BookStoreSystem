package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookBulkAddDto;
import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.dto.response.WarehouseResponseDto;
import com.ifellow.bookstore.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    WarehouseResponseDto save(WarehouseRequestDto warehouseRequestDto);
    Warehouse findWarehouseById(Long id);
    void addBookToWarehouse(Long id, Long bookId, int quantity);
    void removeBookFromWarehouse(Long id, Long bookId, int quantity);
    Page<WarehouseBookResponseDto> getWarehouseStock(Long id, Pageable pageable);
    void bulkAddBooks(Long id, List<BookBulkAddDto> booksToAdd);
}
