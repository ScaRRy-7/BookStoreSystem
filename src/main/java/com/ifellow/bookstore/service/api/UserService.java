package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    User findUserByUsername(String username) throws UsernameNotFoundException;
    void saveUser(User user);
    User findById(Long id);
    void updateUser(User user);
}
