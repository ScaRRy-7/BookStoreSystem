package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.RoleDto;
import com.ifellow.bookstore.service.api.AdminPanelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adminpanel")
public class AdminPanelController {

    private final AdminPanelService adminPanelService;

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/users/{userId}/role")
    public void setTheRole(@PathVariable Long userId, @Valid @RequestBody RoleDto roleDto) {
        adminPanelService.setTheRole(userId, roleDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/users/{userId}/role")
    public void deleteTheRole(@PathVariable Long userId, @Valid @RequestBody RoleDto roleDto) {
        adminPanelService.deleteTheRole(userId, roleDto);
    }


}
