package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.dto.response.WarehouseResponseDto;
import com.ifellow.bookstore.service.api.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponseDto create(@Valid @RequestBody WarehouseRequestDto warehouseRequestDto) {
        return warehouseService.save(warehouseRequestDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WarehouseResponseDto findById(@PathVariable("id") Long id) {
        return warehouseService.findById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/stock/add")
    public void addBookToWarehouse(@PathVariable Long id, @Valid @RequestBody BookBulkDto bookBulkDto) {
        warehouseService.addBookToWarehouse(id, bookBulkDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/stock/remove")
    public void removeBooksFromWarehouse(@PathVariable Long id, @Valid @RequestBody BookBulkDto bookBulkDto) {
        warehouseService.removeBookFromWarehouse(id, bookBulkDto);
    }

    @GetMapping("/{id}/stock")
    @ResponseStatus(HttpStatus.OK)
    public Page<WarehouseBookResponseDto> getWarehouseStock(@PathVariable Long id, Pageable pageable) {
        return warehouseService.getWarehouseStock(id, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/stock/bulk-add")
    public void addBooksToWarehouse(@PathVariable Long id, @Valid @RequestBody List<BookBulkDto> bookBulkDtos) {
        warehouseService.addBooksToWarehouse(id, bookBulkDtos);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/stock/bulk-remove")
    public void removeBooksFromWarehouse(@PathVariable Long id, @Valid @RequestBody List<BookBulkDto> bookBulkDtos) {
        warehouseService.removeBooksFromWarehouse(id, bookBulkDtos);
    }
}
