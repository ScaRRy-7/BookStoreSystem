package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleService {

    SaleResponseDto processSale(Long storeId, List<BookSaleDto> bookSaleDtoList);
    Page<SaleResponseDto> findBySaleDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<SaleResponseDto> findByStoreId(Long storeId, Pageable pageable);
    Page<SaleResponseDto> findByStoreIdAndSaleDateTimeBetween(Long storeId, LocalDateTime start, LocalDateTime end, Pageable pageable);
    SaleResponseDto findById(Long id);
}
