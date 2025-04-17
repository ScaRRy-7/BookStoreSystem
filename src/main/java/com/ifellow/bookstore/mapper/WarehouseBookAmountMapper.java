package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.model.WarehouseBookAmount;
import org.springframework.stereotype.Component;

@Component
public class WarehouseBookAmountMapper {

    public WarehouseBookResponseDto toResponseDto(WarehouseBookAmount warehouseBookAmount) {
        return new WarehouseBookResponseDto(warehouseBookAmount.getId(),
                warehouseBookAmount.getBook().getTitle(),
                warehouseBookAmount.getBook().getAuthor().getFullName(),
                warehouseBookAmount.getBook().getGenre().getName(),
                warehouseBookAmount.getBook().getPrice(),
                warehouseBookAmount.getAmount());
    }
}
