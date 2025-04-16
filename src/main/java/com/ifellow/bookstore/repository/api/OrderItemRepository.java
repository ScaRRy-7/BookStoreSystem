package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
    Optional<OrderItem> findByOrderIdAndBookId(Long orderId, Long bookId);
}
