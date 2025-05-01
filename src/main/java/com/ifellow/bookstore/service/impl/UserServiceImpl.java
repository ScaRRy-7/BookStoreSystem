package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.response.UserResponseDto;
import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.exception.UserException;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.repository.UserRepository;
import com.ifellow.bookstore.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).toList());
    }

    @Override
    public User findUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new UserException("A user with this username already exists!");

        userRepository.save(user);
    }

    @Override
    public UserResponseDto getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) authentication.getDetails();

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return new UserResponseDto(
                userDetails.getUsername() + " remoteAddress: " + webAuthenticationDetails.getRemoteAddress() + " sessionId" + webAuthenticationDetails.getSessionId() , userDetails.getAuthorities().stream().map(authority -> RoleName.valueOf(authority.getAuthority())).toList());
    }
}
