package com.ifellow.bookstore.util;

import com.ifellow.bookstore.model.SaleItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class SaleUtils {

    public BigDecimal calculateTotalPrice(List<SaleItem> saleItemList) {
        return saleItemList
                .stream()
                .map(saleItem -> saleItem.getPrice().multiply(new BigDecimal(saleItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
