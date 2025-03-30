package com.ifellow.bookstore.service.interfaces;

import com.ifellow.bookstore.dto.request.BookRequestDto;

import java.util.List;
import java.util.UUID;

public interface TransferService {

    void transferToStore(UUID storeId, List<BookRequestDto> bookRequestDtos);
}
