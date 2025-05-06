package integration.repository;

import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.model.Role;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.repository.RoleRepository;
import com.ifellow.bookstore.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = RootConfiguration.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Role role;

    @BeforeEach
    void setUp() {
        role = roleRepository.findByName(RoleName.ROLE_CLIENT).get();
    }

    @Test
    @DisplayName("Сохранение пользователя с валидными данными")
    public void saveUser_ValidData_PersistsData() {
        roleRepository.save(role);
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .roles(Set.of(role))
                .build();

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals(Set.of(role), savedUser.getRoles());
    }

    @Test
    @DisplayName("Сохранение пользователя с дублирующимся именем")
    public void saveUser_DuplicateUsername_ThrowsException() {
        User user1 = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();
        userRepository.save(user1);
        User user2 = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user2);
        });
    }

    @Test
    @DisplayName("Поиск пользователя по ID")
    public void findById_ExistingId_ReturnsUser() {
        roleRepository.save(role);
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .roles(Set.of(role))
                .build();
        entityManager.persist(user);

        Optional<User> foundUser = userRepository.findById(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals(Set.of(role), foundUser.get().getRoles());
    }

    @Test
    @DisplayName("Поиск всех пользователей")
    public void findAll_MultipleUsers_ReturnsAllUsers() {
        roleRepository.save(role);
        User user1 = User.builder()
                .username("user1")
                .password("encodedPassword")
                .roles(Set.of(role))
                .build();
        User user2 = User.builder()
                .username("user2")
                .password("encodedPassword")
                .roles(Set.of(role))
                .build();
        entityManager.persist(user1);
        entityManager.persist(user2);

        List<User> users = userRepository.findAll();

        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user1")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user2")));
    }

    @Test
    @DisplayName("Удаление пользователя")
    public void deleteUser_ExistingUser_RemovesData() {
        roleRepository.save(role);
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .roles(Set.of(role))
                .build();
        entityManager.persist(user);

        userRepository.delete(user);
        Optional<User> foundUser = userRepository.findById(user.getId());

        assertFalse(foundUser.isPresent());
    }
}