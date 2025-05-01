package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.response.UserResponseDto;
import com.ifellow.bookstore.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getProfile() {
        return userService.getProfile();
    }
}
