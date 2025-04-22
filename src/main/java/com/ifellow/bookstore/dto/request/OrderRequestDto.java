package com.ifellow.bookstore.dto.request;

import java.util.List;

public record OrderRequestDto(
        Long warehouseId,
        List<BookOrderDto> bookOrderDtoList
) {
}
