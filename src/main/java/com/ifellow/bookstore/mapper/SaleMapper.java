package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.model.Sale;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

    public SaleResponseDto toResponseDto(Sale sale) {
        return new SaleResponseDto(
                sale.getId(),
                sale.getSaleDateTime(),
                sale.getStore().getId(),
                sale.getTotalPrice()
        );
    }
}
