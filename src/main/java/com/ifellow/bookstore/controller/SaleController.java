package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.service.api.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/sales")
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/process/{storeId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SaleResponseDto processSale(@PathVariable Long storeId, @RequestBody List<BookSaleDto> bookSaleDtoList) {
        return saleService.processSale(storeId, bookSaleDtoList);
    }

    @GetMapping("/timeperiod")
    @ResponseStatus(HttpStatus.OK)
    public Page<SaleResponseDto> findBySaleDateTimeBetween(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end, Pageable pageable) {
        return saleService.findBySaleDateTimeBetween(start, end, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SaleResponseDto findById(@PathVariable Long id) {
        return saleService.findById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/store/{storeId}")
    public Page<SaleResponseDto> findByStoreId(@PathVariable Long storeId, Pageable pageable) {
        return saleService.findByStoreId(storeId, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/timeperiod/store/{storeId}")
    public Page<SaleResponseDto> findByStoreIdAndSaleDateTimeBetween
            (@PathVariable Long storeId, @RequestParam LocalDateTime start, @RequestParam LocalDateTime end, Pageable pageable) {
        return saleService.findByStoreIdAndSaleDateTimeBetween(storeId, start, end, pageable);
    }
}
