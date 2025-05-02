package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.filter.SaleFilter;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SaleService {

    SaleResponseDto processSale(Long storeId, List<BookSaleDto> bookSaleDtoList);
    SaleResponseDto findById(Long id);
    Page<SaleResponseDto> findAll(SaleFilter filter, Pageable pageable);
    Page<SaleResponseDto> findByUserId(Long userId, Pageable pageable);
}
