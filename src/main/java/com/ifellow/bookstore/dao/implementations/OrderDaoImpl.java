package com.ifellow.bookstore.dao.implementations;

import com.ifellow.bookstore.dao.interfaces.OrderDao;
import com.ifellow.bookstore.datasource.DataSource;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.model.Order;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderDaoImpl implements OrderDao {

    private final DataSource dataSource;

    @Override
    public void save(Order order) {
        dataSource.getOrders().add(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return dataSource.getOrders().stream()
                .filter(order -> order.getId().equals(id))
                .findFirst();
    }

    @Override
    public void updateStatus(UUID orderId, OrderStatus status) {
        findById(orderId).ifPresent(order -> order.setStatus(status));
    }
}
