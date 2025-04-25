package com.ifellow.bookstore.specification;

import com.ifellow.bookstore.dto.request.OrderFilter;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.model.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> withFilter(OrderFilter filter) {
        return Specification.where(byDateTimeBetween(filter.getBeginTime(), filter.getEndTime()))
                .and(byStatus(filter.getStatus()))
                .and(byWarehouseId(filter.getWarehouseId()));
    }

    private static Specification<Order> byDateTimeBetween(LocalDateTime beginTime, LocalDateTime endTime) {
        if (beginTime == null && endTime == null) return null;
        return (root, query, cb) -> {
            if (beginTime != null && endTime != null) {
                return cb.between(root.get("orderDateTime"), beginTime, endTime);
            } else if (beginTime != null) {
                return cb.greaterThan(root.get("orderDateTime"), beginTime);
            } else {
                return cb.lessThan(root.get("orderDateTime"), endTime);
            }
        };
    }

    private static Specification<Order> byStatus(OrderStatus status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("orderStatus"), status);
    }

    private static Specification<Order> byWarehouseId(Long warehouseId) {
        if (warehouseId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("warehouse").get("id"), warehouseId);
    }
}
