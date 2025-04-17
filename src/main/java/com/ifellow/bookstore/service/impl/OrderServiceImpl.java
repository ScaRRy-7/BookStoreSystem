package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.BookOrderDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.exception.ChangeOrderStatusException;
import com.ifellow.bookstore.exception.OrderNotFoundException;
import com.ifellow.bookstore.mapper.OrderMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Order;
import com.ifellow.bookstore.model.OrderItem;
import com.ifellow.bookstore.model.Warehouse;
import com.ifellow.bookstore.repository.api.OrderRepository;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.OrderService;
import com.ifellow.bookstore.service.api.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WarehouseService warehouseService;
    private final BookService bookService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponseDto create(Long warehouseId, List<BookOrderDto> bookOrderDtoList) {
        Warehouse warehouse = warehouseService.findWarehouseById(warehouseId);

        Order order = new Order();
        order.setWarehouse(warehouse);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CREATED);

        for (BookOrderDto bookOrderDto : bookOrderDtoList) {
            Book book = bookService.findBookById(bookOrderDto.bookId());
            warehouseService.removeBookFromWarehouse(warehouseId, bookOrderDto.bookId(), bookOrderDto.quantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(bookOrderDto.quantity());
            orderItem.setPrice(book.getPrice());

            order.getOrderItemList().add(orderItem);
        }

        order.setTotalPrice(calculateTotalPrice(order.getOrderItemList()));
        orderRepository.save(order);

        return orderMapper.toResponseDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto completeById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != OrderStatus.CREATED)
            throw new ChangeOrderStatusException("Order can't be completed because it isn't CREATED");

        order.setOrderStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        return orderMapper.toResponseDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto cancelById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != OrderStatus.CREATED)
            throw new ChangeOrderStatusException("Order can't be canceled because it isn't CREATED");

        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return orderMapper.toResponseDto(order);
    }

    @Override
    public OrderResponseDto findById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        return orderMapper.toResponseDto(order);
    }

    @Override
    public Page<OrderResponseDto> findByOrderStatus(OrderStatus orderStatus, Pageable pageable) {
        return orderRepository.findByOrderStatus(orderStatus, pageable).map(orderMapper::toResponseDto);
    }

    @Override
    public Page<OrderResponseDto> findByOrderDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return orderRepository.findByOrderDateTimeBetween(start, end, pageable).map(orderMapper::toResponseDto);
    }

    @Override
    public Page<OrderResponseDto> findByWarehouseId(Long warehouseId, Pageable pageable) {
        return orderRepository.findByWarehouseId(warehouseId, pageable).map(orderMapper::toResponseDto);
    }

    @Override
    public Page<OrderResponseDto> findByWarehouseIdAndOrderStatus(Long warehouseId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByWarehouseIdAndOrderStatus(warehouseId, status, pageable).map(orderMapper::toResponseDto);
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItemList) {
            BigDecimal totalPriceOfOrderItem = orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
            totalPrice = totalPrice.add(totalPriceOfOrderItem);
        }
        return totalPrice;
    }

}
