package integration.repository;

import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.enumeration.RoleName;
import com.ifellow.bookstore.model.Role;
import com.ifellow.bookstore.repository.RoleRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = RootConfiguration.class)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Поиск роли по имени")
    public void findByName_ExistingName_ReturnsRole() {
        Optional<Role> foundRole = roleRepository.findByName(RoleName.ROLE_CLIENT);

        assertTrue(foundRole.isPresent());
        assertEquals(RoleName.ROLE_CLIENT, foundRole.get().getName());
    }

    @Test
    @DisplayName("Проверка существования роли по имени")
    public void existsByName_ExistingName_ReturnsTrue() {
        boolean exists = roleRepository.existsByName(RoleName.ROLE_CLIENT);

        assertTrue(exists);
    }

}