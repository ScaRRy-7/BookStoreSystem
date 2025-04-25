package com.ifellow.bookstore.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleFilter {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long storeId;
}
