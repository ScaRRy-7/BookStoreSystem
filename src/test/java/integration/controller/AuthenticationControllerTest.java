package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.JwtRequest;
import com.ifellow.bookstore.dto.request.RefreshTokenDto;
import com.ifellow.bookstore.dto.request.RegistrationUserDto;
import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.model.RefreshToken;
import com.ifellow.bookstore.model.Role;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.repository.RefreshTokenRepository;
import com.ifellow.bookstore.repository.RoleRepository;
import com.ifellow.bookstore.repository.UserRepository;
import com.ifellow.bookstore.service.api.AuthenticationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = RootConfiguration.class)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    User user;

    @BeforeEach
    public void setUp() {
        Role clientRole = roleRepository.findByName(RoleName.ROLE_CLIENT).get();

        user = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password123"))
                .roles(Set.of(clientRole))
                .build();
        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        refreshTokenRepository.deleteAll();
        userRepository.delete(user);
    }

    @Test
    @DisplayName("Логин с валидными учетными данными возвращает токены")
    public void login_ValidCredentials_ReturnsTokens() throws Exception {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setUsername("testuser");
        jwtRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("Логин с невалидными учетными данными возвращает ошибку")
    public void login_InvalidCredentials_ReturnsError() throws Exception {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setUsername("testuser");
        jwtRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Регистрация с валидными данными создает пользователя")
    public void register_ValidData_CreatesUser() throws Exception {
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("newuser");
        registrationUserDto.setPassword("password123");
        registrationUserDto.setConfirmPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Register successful! Go to login page to get access + refresh tokens!"));
    }

    @Test
    @DisplayName("Регистрация с несовпадающими паролями возвращает ошибку")
    public void register_MismatchedPasswords_ReturnsError() throws Exception {
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("newuser");
        registrationUserDto.setPassword("password123");
        registrationUserDto.setConfirmPassword("different123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Password does not match!"));
    }

    @Test
    @DisplayName("Обновление токена с валидным refresh-токеном возвращает новые токены")
    public void refresh_ValidToken_ReturnsNewTokens() throws Exception {
        User user = userRepository.findByUsername("testuser").orElseThrow();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token("valid-refresh-token")
                .revoked(false)
                .refreshExpiration(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant())
                .build();
        refreshTokenRepository.save(refreshToken);

        RefreshTokenDto refreshTokenDto = new RefreshTokenDto("valid-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("Обновление токена с невалидным refresh-токеном возвращает ошибку")
    public void refresh_InvalidToken_ReturnsError() throws Exception {
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto("invalid-refresh-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Refresh token not found with UUID: invalid-refresh-token"));
    }

    @Test
    @DisplayName("Логаут с валидным refresh-токеном завершает сессию")
    public void logout_ValidToken_CompletesLogout() throws Exception {
        User user = userRepository.findByUsername("testuser").orElseThrow();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token("valid-refresh-token")
                .revoked(false)
                .refreshExpiration(LocalDateTime.now().plusDays(7).atZone(ZoneId.systemDefault()).toInstant())
                .build();
        refreshTokenRepository.save(refreshToken);

        RefreshTokenDto refreshTokenDto = new RefreshTokenDto("valid-refresh-token");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Logout successful!"));
    }
}