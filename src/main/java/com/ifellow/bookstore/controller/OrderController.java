package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.dto.filter.OrderFilter;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.service.api.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/warehouses/{warehouseId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto create(@PathVariable Long warehouseId, @Valid @RequestBody List<BookOrderDto> bookOrderDtoList) {
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

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponseDto> findAll(@ModelAttribute OrderFilter filter, Pageable pageable) {
        return orderService.findAll(filter, pageable);
    }
}
