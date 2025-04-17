package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.model.Store;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

    public Store toEntity(StoreRequestDto storeRequestDto) {
        Store store = new Store();
        store.setAddress(storeRequestDto.address());
        return store;
    }

    public StoreResponseDto toResponseDto(Store store) {
        return new StoreResponseDto(store.getId(), store.getAddress());
    }
}
