package com.ifellow.bookstore.initializer;

import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.repository.UserRepository;
import com.ifellow.bookstore.service.api.RoleService;
import com.ifellow.bookstore.util.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UsersInitializer {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final JwtUtils jwtUtils;

    @PostConstruct
    public void initUsers() {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Adminpass10"))
                .roles(Set.of(roleService.findByName(RoleName.ROLE_ADMIN)))
                .build();

        User manager = User.builder()
                .username("manager")
                .password(passwordEncoder.encode("managerpass10"))
                .roles(Set.of(roleService.findByName(RoleName.ROLE_MANAGER)))
                .build();

        User client = User.builder()
                .username("client")
                .password(passwordEncoder.encode("Clientpass10"))
                .roles(Set.of(roleService.findByName(RoleName.ROLE_CLIENT)))
                .build();

        userRepository.save(admin);
        userRepository.save(manager);
        userRepository.save(client);

        String adminAccessToken = jwtUtils.generateLongLivedAccessTokenForInitializingUsers(admin);
        String managerAccessToken = jwtUtils.generateLongLivedAccessTokenForInitializingUsers(manager);
        String clientAccessToken = jwtUtils.generateLongLivedAccessTokenForInitializingUsers(client);

        System.out.println("admin access token = " + adminAccessToken);
        System.out.println("manager access token = " + managerAccessToken);
        System.out.println("client access token = " + clientAccessToken);

    }
}
