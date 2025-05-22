package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.exception.RoleException;
import com.ifellow.bookstore.model.Role;
import com.ifellow.bookstore.repository.RoleRepository;
import com.ifellow.bookstore.service.api.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Role findByName(RoleName roleName) throws RoleException {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleException("Role not found by name: " + roleName.name()));
    }
}
