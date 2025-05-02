package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.RoleDto;

public interface AdminPanelService {
    void setTheRole(Long userId, RoleDto roleDto);
    void deleteTheRole(Long userId, RoleDto roleDto);
}
