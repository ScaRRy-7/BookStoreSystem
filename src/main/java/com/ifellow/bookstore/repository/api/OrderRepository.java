package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);
    Page<Order> findByOrderDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Order> findByWarehouseId(Long warehouseId, Pageable pageable);
    Page<Order> findByWarehouseIdAndOrderStatus(Long warehouseId, OrderStatus status, Pageable pageable);
}
