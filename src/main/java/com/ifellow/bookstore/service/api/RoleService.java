package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.model.Role;

public interface RoleService {
    Role findByName(RoleName name);
}
