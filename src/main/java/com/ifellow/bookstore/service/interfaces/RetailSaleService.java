package com.ifellow.bookstore.service.interfaces;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.ReceiptResponseDto;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreNotFoundException;

import java.util.List;
import java.util.UUID;

public interface RetailSaleService {

    ReceiptResponseDto processSale(UUID storeId, List<BookRequestDto> books)
            throws StoreNotFoundException, NotEnoughStockException;
}
