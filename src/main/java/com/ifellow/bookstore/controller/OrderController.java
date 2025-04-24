package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.service.api.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/warehouses/{warehouseId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto create(@PathVariable Long warehouseId, @RequestBody List<BookOrderDto> bookOrderDtoList) {
        return orderService.create(warehouseId, bookOrderDtoList);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/orders/{orderId}/complete")
    public OrderResponseDto completeById(@PathVariable Long orderId) {
        return orderService.completeById(orderId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/orders/{orderId}/cancel")
    public OrderResponseDto cancelById(@PathVariable Long orderId) {
        return orderService.cancelById(orderId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/orders/{orderId}")
    public OrderResponseDto findById(@PathVariable Long orderId) {
        return orderService.findById(orderId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/orders/by-order-status")
    public Page<OrderResponseDto> findByOrderStatus(@RequestParam OrderStatus orderStatus, Pageable pageable) {
        return orderService.findByOrderStatus(orderStatus, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/orders/by-datetime-between")
    public Page<OrderResponseDto> findByOrderDateBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return orderService.findByOrderDateTimeBetween(startDate, endDate, pageable);
    }

    @GetMapping("/warehouses/{warehouseId}/orders")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponseDto> findByWarehouseId(@PathVariable Long warehouseId, Pageable pageable) {
        return orderService.findByWarehouseId(warehouseId, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/warehouses/{warehouseId}/orders/by-order-status")
    public Page<OrderResponseDto> findByWarehouseIdAndOrderStatus(@PathVariable Long warehouseId, @RequestParam OrderStatus orderStatus, Pageable pageable) {
        return orderService.findByWarehouseIdAndOrderStatus(warehouseId, orderStatus, pageable);
    }


}
