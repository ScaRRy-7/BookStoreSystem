package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.OrderRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Order;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponseDto toResponseDto(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getStoreId(),
                order.getCreatedDate(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getBooks().stream().map(BookMapper::toResponseDTO)
                        .collect(Collectors.toList())
        );
    }
}
