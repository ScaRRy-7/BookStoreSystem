package com.ifellow.bookstore.util;

import com.ifellow.bookstore.model.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderUtils {

    public BigDecimal calculateTotalPrice(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .reduce(BigDecimal.ZERO, (BigDecimal totalPrice, OrderItem orderItem) ->
                                totalPrice.add(orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))),
                        BigDecimal::add);

    }
}
