package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponseDto toResponseDto(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getOrderDateTime(),
                order.getOrderStatus(),
                order.getWarehouse().getId(),
                order.getTotalPrice()
        );
    }
}
