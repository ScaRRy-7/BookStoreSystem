package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileService {

    UserResponseDto getProfile();
    Page<OrderResponseDto> findProfileOrders(Pageable pageable);
    Page<SaleResponseDto> findProfileSales(Pageable pageable);
}
