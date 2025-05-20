package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.StoreBookResponseDto;
import com.ifellow.bookstore.model.StoreBookAmount;
import org.springframework.stereotype.Component;

@Component
public class StoreBookAmountMapper implements ToDtoMapper<StoreBookAmount, StoreBookResponseDto> {

    @Override
    public StoreBookResponseDto toDto(StoreBookAmount storeBookAmount) {
        return new StoreBookResponseDto(storeBookAmount.getId(),
                storeBookAmount.getStore().getId(),
                storeBookAmount.getBook().getId(),
                storeBookAmount.getAmount());
    }
}
