package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.dto.filter.OrderFilter;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponseDto create(Long warehouseId, List<BookOrderDto> bookOrderDtoList);
    OrderResponseDto completeById(Long orderId);
    OrderResponseDto cancelById(Long orderId);
    OrderResponseDto findById(Long orderId);
    Page<OrderResponseDto> findAll(OrderFilter filter, Pageable pageable);
}
