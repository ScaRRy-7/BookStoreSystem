package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    OrderResponseDto create(Long warehouseId, List<BookOrderDto> bookOrderDtoList);
    OrderResponseDto completeById(Long orderId);
    OrderResponseDto cancelById(Long orderId);
    OrderResponseDto findById(Long orderId);
    Page<OrderResponseDto> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);
    Page<OrderResponseDto> findByOrderDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<OrderResponseDto> findByWarehouseId(Long warehouseId, Pageable pageable);
    Page<OrderResponseDto> findByWarehouseIdAndOrderStatus(Long warehouseId, OrderStatus status, Pageable pageable);

}
