package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.model.Store;

import java.util.UUID;

public class StoreMapper {

    public static Store toModel(StoreRequestDto storeRequestDto) {
        return new Store(
                UUID.randomUUID(),
                storeRequestDto.name(),
                storeRequestDto.address()
        );
    }

    public static StoreResponseDto toResponseDto(Store store) {
        return new StoreResponseDto(
          store.getId(),
          store.getName(),
          store.getAddress()
        );
    }
}
