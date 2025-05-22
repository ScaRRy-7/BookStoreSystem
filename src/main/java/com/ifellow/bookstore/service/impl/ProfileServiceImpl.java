package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.dto.response.UserResponseDto;
import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.service.api.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final OrderService orderService;
    private final SaleService saleService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Override
    public UserResponseDto getProfile() {
        UserDetails userDetails = authenticationService.getCurrentPrincipal();

        return new UserResponseDto(
                userDetails.getUsername(),

                userDetails
                        .getAuthorities()
                        .stream()
                        .map(authority -> RoleName.valueOf(authority.getAuthority()))
                        .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> findProfileOrders(Pageable pageable) {
        UserDetails userDetails = authenticationService.getCurrentPrincipal();
        Long userId = userService.findUserByUsername(userDetails.getUsername()).getId();

        return orderService.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponseDto> findProfileSales(Pageable pageable) {
        UserDetails userDetails = authenticationService.getCurrentPrincipal();
        Long userId = userService.findUserByUsername(userDetails.getUsername()).getId();

        return saleService.findByUserId(userId, pageable);
    }
}