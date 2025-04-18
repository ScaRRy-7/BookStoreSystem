package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.StoreBookResponseDto;
import com.ifellow.bookstore.model.StoreBookAmount;
import org.springframework.stereotype.Component;

@Component
public class StoreBookAmountMapper {

    public StoreBookResponseDto toResponseDto(StoreBookAmount storeBookAmount) {
        return new StoreBookResponseDto(storeBookAmount.getId(),
                storeBookAmount.getBook().getId(),
                storeBookAmount.getStore().getId(),
                storeBookAmount.getAmount());
    }
}
