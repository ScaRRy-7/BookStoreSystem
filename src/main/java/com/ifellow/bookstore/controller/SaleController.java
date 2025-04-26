package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.filter.SaleFilter;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.service.api.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/process/{storeId}")
    @ResponseStatus(HttpStatus.OK)
    public SaleResponseDto processSale(@PathVariable Long storeId, @Valid @RequestBody List<BookSaleDto> bookSaleDtoList) {
        return saleService.processSale(storeId, bookSaleDtoList);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SaleResponseDto findById(@PathVariable Long id) {
        return saleService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<SaleResponseDto> findAll(@RequestBody SaleFilter filter, Pageable pageable) {
        return saleService.findAll(filter, pageable);
    }
}
