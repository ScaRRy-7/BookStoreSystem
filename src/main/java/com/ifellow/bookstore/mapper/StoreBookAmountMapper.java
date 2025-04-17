package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.StoreBookResponseDto;
import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.model.StoreBookAmount;
import com.ifellow.bookstore.model.WarehouseBookAmount;
import org.springframework.stereotype.Component;

@Component
public class StoreBookAmountMapper {

    public StoreBookResponseDto toResponseDto(StoreBookAmount storeBookAmount) {
        return new StoreBookResponseDto(storeBookAmount.getId(),
                storeBookAmount.getBook().getTitle(),
                storeBookAmount.getBook().getAuthor().getFullName(),
                storeBookAmount.getBook().getGenre().getName(),
                storeBookAmount.getBook().getPrice(),
                storeBookAmount.getAmount());
    }
}
