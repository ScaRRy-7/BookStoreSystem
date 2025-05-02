package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.model.Sale;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper implements ToDtoMapper<Sale, SaleResponseDto> {

    public SaleResponseDto toDto(Sale sale) {
        return new SaleResponseDto(
                sale.getId(),
                sale.getUser().getId(),
                sale.getSaleDateTime(),
                sale.getStore().getId(),
                sale.getTotalPrice()
        );
    }
}
