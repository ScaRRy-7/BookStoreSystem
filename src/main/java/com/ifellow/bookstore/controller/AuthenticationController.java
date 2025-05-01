    package com.ifellow.bookstore.controller;

    import com.ifellow.bookstore.dto.request.JwtRequest;
    import com.ifellow.bookstore.dto.request.RefreshTokenDto;
    import com.ifellow.bookstore.dto.request.RegistrationUserDto;
    import com.ifellow.bookstore.dto.response.JwtResponse;
    import com.ifellow.bookstore.service.api.AuthenticationService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api/auth")
    public class AuthenticationController {

        private final AuthenticationService authenticationService;

        @PostMapping("/login")
        @ResponseStatus(HttpStatus.OK)
        public JwtResponse login(@Valid @RequestBody JwtRequest jwtRequest) {
            return authenticationService.login(jwtRequest);
        }

        @PostMapping("/refresh")
        @ResponseStatus(HttpStatus.OK)
        public JwtResponse refresh(@Valid @RequestBody RefreshTokenDto refreshToken) {
            return authenticationService.refresh(refreshToken);
        }

        @PostMapping("/logout")
        @ResponseStatus(HttpStatus.OK)
        public String logout(@Valid @RequestBody RefreshTokenDto refreshToken) {
            return authenticationService.logout(refreshToken);
        }

        @PostMapping("/register")
        @ResponseStatus(HttpStatus.OK)
        public String register(@Valid @RequestBody RegistrationUserDto registrationUserDto) {
            return authenticationService.register(registrationUserDto);
        }
    }
