package com.ifellow.bookstore.dto.filter;

import com.ifellow.bookstore.enumeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilter {
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private OrderStatus status;
    private Long warehouseId;
}
