package com.ifellow.bookstore.service.interfaces;

import com.ifellow.bookstore.dto.request.OrderRequestDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreNotFoundException;

import java.util.UUID;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto)
        throws StoreNotFoundException, NotEnoughStockException;

    void cancelOrder(UUID orderId);
    OrderResponseDto getOrder(UUID orderId);
}
