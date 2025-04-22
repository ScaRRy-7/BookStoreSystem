package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.OrderRequestDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto create(@RequestBody OrderRequestDto orderRequestDto) {
        return orderService.create(orderRequestDto.warehouseId(), orderRequestDto.bookOrderDtoList());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{orderId}/complete")
    public OrderResponseDto complete(@PathVariable("orderId") Long orderId) {
        return orderService.completeById(orderId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{orderId}/cancel")
    public OrderResponseDto cancel(@PathVariable("orderId") Long orderId) {
        return orderService.cancelById(orderId);
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponseDto getOrderById(@PathVariable("orderId") Long orderId) {
        return orderService.findById(orderId);
    }

    @GetMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponseDto> findByOrderStatus(@RequestParam OrderStatus orderStatus, Pageable pageable) {
        return orderService.findByOrderStatus(orderStatus, pageable);
    }

    @GetMapping("/timeperiod")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponseDto> findByOrderDateTimeBetween(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end, Pageable pageable) {
        return orderService.findByOrderDateTimeBetween(start, end, pageable);
    }

    @GetMapping("/warehouse")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponseDto> findByWarehouseId(@RequestParam Long warehouseId, Pageable pageable) {
        return orderService.findByWarehouseId(warehouseId, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/warehouse-status")
    public Page<OrderResponseDto> findByWarehouseIdAndOrderStatus(@RequestParam Long warehouseId, @RequestParam OrderStatus status, Pageable pageable) {
        return orderService.findByWarehouseIdAndOrderStatus(warehouseId, status, pageable);
    }

}
