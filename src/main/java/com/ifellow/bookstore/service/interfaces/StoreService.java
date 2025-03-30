package com.ifellow.bookstore.service.interfaces;

import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.exception.StoreNotFoundException;

import java.util.List;
import java.util.UUID;

public interface StoreService {

    UUID add(StoreRequestDto storeRequestDto);

    void remove(StoreRequestDto storeRequestDto);

    StoreResponseDto findById(UUID id)
            throws StoreNotFoundException;
}
