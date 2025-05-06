package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.dto.request.RoleDto;
import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.model.Role;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.repository.RoleRepository;
import com.ifellow.bookstore.repository.UserRepository;
import com.ifellow.bookstore.util.JwtUtils;
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

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest(classes = RootConfiguration.class)
public class AdminPanelControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String clientToken;
    private User targetUser;

    @BeforeEach
    public void setUp() {
        Role clientRole = roleRepository.findByName(RoleName.ROLE_CLIENT).get();

        targetUser = User.builder()
                .username("target")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(clientRole))
                .build();
        userRepository.save(targetUser);

        adminToken = jwtUtils.generateAccessTokenFromUsername("admin");
        clientToken = jwtUtils.generateAccessTokenFromUsername("client");
    }

    @AfterEach
    public void tearDown() {
        userRepository.delete(targetUser);

    }

    @Test
    @DisplayName("Установка роли пользователю с ролью ADMIN обновляет роли")
    public void setTheRole_AdminRole_UpdatesUser() throws Exception {
        RoleDto roleDto = new RoleDto("ROLE_MANAGER");

        mockMvc.perform(put("/api/adminpanel/users/{userId}/role", targetUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Установка роли с ролью CLIENT возвращает ошибку")
    public void setTheRole_ClientRole_ReturnsError() throws Exception {
        RoleDto roleDto = new RoleDto("ROLE_MANAGER");

        mockMvc.perform(put("/api/adminpanel/users/{userId}/role", targetUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto))
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Установка роли с несуществующим userId возвращает ошибку")
    public void setTheRole_NonExistentUserId_ReturnsError() throws Exception {
        RoleDto roleDto = new RoleDto("ROLE_MANAGER");

        mockMvc.perform(put("/api/adminpanel/users/404/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Установка несуществующей роли возвращает ошибку")
    public void setTheRole_NonExistentRole_ReturnsError() throws Exception {
        RoleDto roleDto = new RoleDto("ROLE_UNKNOWN");

        mockMvc.perform(put("/api/adminpanel/users/{userId}/role", targetUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Удаление роли у пользователя с ролью ADMIN -  обновляет роли")
    public void deleteTheRole_AdminRole_UpdatesUser() throws Exception {
        RoleDto roleDto = new RoleDto("ROLE_CLIENT");

        mockMvc.perform(delete("/api/adminpanel/users/{userId}/role", targetUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удаление роли с ролью CLIENT - возвращает ошибку")
    public void deleteTheRole_ClientRole_ReturnsError() throws Exception {
        RoleDto roleDto = new RoleDto("ROLE_CLIENT");

        mockMvc.perform(delete("/api/adminpanel/users/{userId}/role", targetUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto))
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Удаление роли с несуществующим userId - возвращает ошибку")
    public void deleteTheRole_NonExistentUserId_ReturnsError() throws Exception {
        String nonExistId = "123";
        RoleDto roleDto = new RoleDto("ROLE_CLIENT");

        mockMvc.perform(delete("/api/adminpanel/users/{nonExistId}/role", nonExistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Удаление несуществующей роли возвращает ошибку")
    public void deleteTheRole_NonExistentRole_ReturnsError() throws Exception {
        RoleDto roleDto = new RoleDto("ROLE_UNKNOWN");

        mockMvc.perform(delete("/api/adminpanel/users/{userId}/role", targetUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }
}