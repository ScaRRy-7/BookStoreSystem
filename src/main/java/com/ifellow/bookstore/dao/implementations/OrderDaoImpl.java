package com.ifellow.bookstore.dao.implementations;

import com.ifellow.bookstore.dao.interfaces.OrderDao;
import com.ifellow.bookstore.datasource.DataSource;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderDaoImpl implements OrderDao {

    private final DataSource dataSource;

    public OrderDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    @Override
    public Optional<OrderStatus> getStatusByOrderId(UUID orderId) {
        return dataSource.getOrders().stream()
                .filter(order -> order.getId().equals(orderId))
                .map(Order::getStatus)
                .findFirst();
    }
}
