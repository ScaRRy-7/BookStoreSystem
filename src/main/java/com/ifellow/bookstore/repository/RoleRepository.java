package com.ifellow.bookstore.repository;

import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
    boolean existsByName(RoleName name);
}
