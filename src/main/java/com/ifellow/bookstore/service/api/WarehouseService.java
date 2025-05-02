package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.dto.response.WarehouseResponseDto;
import com.ifellow.bookstore.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    WarehouseResponseDto save(WarehouseRequestDto warehouseRequestDto);
    WarehouseResponseDto findById(Long id);
    Warehouse findWarehouseById(Long id);
    void addBookToWarehouse(Long id, BookBulkDto bookBulkDto);
    void removeBookFromWarehouse(Long id, BookBulkDto bookBulkDto);
    Page<WarehouseBookResponseDto> getWarehouseStock(Long id, Pageable pageable);
    void addBooksToWarehouse(Long id, List<BookBulkDto> booksToAdd);
    void removeBooksFromWarehouse(Long id, List<BookBulkDto> booksToRemove);
}
