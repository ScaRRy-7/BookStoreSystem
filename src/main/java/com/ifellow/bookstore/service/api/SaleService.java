package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.request.SaleFilter;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleService {

    SaleResponseDto processSale(Long storeId, List<BookSaleDto> bookSaleDtoList);
    SaleResponseDto findById(Long id);
    Page<SaleResponseDto> findAll(SaleFilter filter, Pageable pageable);
}
