package com.ifellow.bookstore.specification;

import com.ifellow.bookstore.dto.filter.SaleFilter;
import com.ifellow.bookstore.model.Sale;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class SaleSpecification {

    public static Specification<Sale> withFilter(SaleFilter filter) {
        return Specification.where(byDateTimeBetween(filter.getStartTime(), filter.getEndTime()))
                .and(byStoreId(filter.getStoreId()));
    }

    private static Specification<Sale> byDateTimeBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return null;
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("saleDateTime"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("saleDateTime"), start);
            } else {
                return cb.lessThan(root.get("saleDateTime"), end);
            }
        };
    }

    private static Specification<Sale> byStoreId(Long storeId) {
        if (storeId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("store").get("id"), storeId);
    }
}
