package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.RoleDto;
import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.model.Role;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.service.api.AdminPanelService;
import com.ifellow.bookstore.service.api.RoleService;
import com.ifellow.bookstore.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPanelServiceImpl implements AdminPanelService {

    private final UserService userService;
    private final RoleService roleService;

    @Override
    @Transactional
    public void setTheRole(Long userId, RoleDto roleDto) {
        User user = userService.findById(userId);
        Role role = roleService.findByName(RoleName.valueOf(roleDto.roleName()));
        user.getRoles().add(role);
        userService.updateUser(user);
    }

    @Override
    @Transactional
    public void deleteTheRole(Long userId, RoleDto roleDto) {
        User user = userService.findById(userId);
        Role role = roleService.findByName(RoleName.valueOf(roleDto.roleName()));

        user.getRoles().remove(role);
    }
}
