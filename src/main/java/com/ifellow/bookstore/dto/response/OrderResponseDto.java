package com.ifellow.bookstore.dto.response;

import com.ifellow.bookstore.enumeration.OrderStatus;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
        UUID id,
        UUID storeId,
        Date createdDate,
        OrderStatus orderStatus,
        double totalAmount,
        List<BookResponseDto> books) {}
