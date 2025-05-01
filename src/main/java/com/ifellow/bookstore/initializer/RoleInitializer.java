package com.ifellow.bookstore.initializer;

import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.model.Role;
import com.ifellow.bookstore.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRepository roleRepository;

    @PostConstruct
    @Transactional
    public void init() {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.save(new Role(null, roleName));
        }
    }
}
