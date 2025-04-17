package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.dto.response.WarehouseResponseDto;
import com.ifellow.bookstore.model.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public Warehouse toEntity(WarehouseRequestDto warehouseRequestDto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setAddress(warehouseRequestDto.address());
        return warehouse;
    }

    public WarehouseResponseDto toResponseDto(Warehouse warehouse) {
        return new WarehouseResponseDto(warehouse.getId(), warehouse.getAddress());
    }
}
