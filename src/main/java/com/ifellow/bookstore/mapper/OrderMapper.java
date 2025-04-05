package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.model.Order;

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
