package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.dto.response.UserResponseDto;
import com.ifellow.bookstore.service.api.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getProfile() {
        return profileService.getProfile();
    }

    @GetMapping("/my-orders")
    @ResponseStatus(HttpStatus.OK)
    public Page<OrderResponseDto> findProfileOrders(Pageable pageable) {
        return profileService.findProfileOrders(pageable);
    }

    @GetMapping("my-sales") //потерял слэш)
    @ResponseStatus(HttpStatus.OK)
    public Page<SaleResponseDto> findProfileSales(Pageable pageable) {
        return profileService.findProfileSales(pageable);
    }
}
