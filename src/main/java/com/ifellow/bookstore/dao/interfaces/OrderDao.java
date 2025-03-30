package com.ifellow.bookstore.dao.interfaces;

import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderDao {
    void save(Order order);
    Optional<Order> findById(UUID id);
    void updateStatus(UUID orderId, OrderStatus status);
}
