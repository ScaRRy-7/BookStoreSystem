package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.model.WarehouseBookAmount;
import org.springframework.stereotype.Component;

@Component
public class WarehouseBookAmountMapper implements ToDtoMapper<WarehouseBookAmount, WarehouseBookResponseDto> {

    @Override
    public WarehouseBookResponseDto toDto(WarehouseBookAmount warehouseBookAmount) {
        return new WarehouseBookResponseDto(warehouseBookAmount.getId(),
                warehouseBookAmount.getWarehouse().getId(),
                warehouseBookAmount.getBook().getId(),
                warehouseBookAmount.getAmount());
    }
}
