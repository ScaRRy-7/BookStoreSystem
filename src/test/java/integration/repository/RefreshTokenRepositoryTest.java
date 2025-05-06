package integration.repository;

import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.model.RefreshToken;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.repository.RefreshTokenRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(classes = RootConfiguration.class)
class RefreshTokenRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private RefreshTokenRepository repository;

    @Test
    @DisplayName("findByToken возвращает токен")
    void findByToken_ReturnsToken() {
        User user = User.builder()
                .username("testuser")
                .password("somepassword")
                .build();
        entityManager.persist(user);
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token("token123")
                .refreshExpiration(Instant.now().plusSeconds(1000))
                .build();
        entityManager.persist(token);

        Optional<RefreshToken> result = repository.findByToken("token123");

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("findByUser возвращает токен")
    void findByUser_ReturnsToken() {
        User user = User.builder().
                username("testuser")
                .password("somepassword")
                .build();
        entityManager.persist(user);
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token("token123")
                .refreshExpiration(Instant.now().plusSeconds(3600))
                .build();
        entityManager.persist(token);

        Optional<RefreshToken> result = repository.findByUser(user);

        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo("token123");
    }

    @Test
    @DisplayName("deleteByUser удаляет токен")
    void deleteByUser_RemovesToken() {
        User user = User.builder()
                .username("testuser")
                .password("somepassword")
                .build();
        entityManager.persist(user);
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token("token123")
                .refreshExpiration(Instant.now().plusSeconds(1000))
                .build();
        entityManager.persist(token);

        repository.deleteByUser(user);

        Optional<RefreshToken> result = repository.findByUser(user);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deleteExpiredAndRevokedTokens удаляет токены")
    void deleteExpiredAndRevokedTokens_RemovesTokens() {
        User user = User.builder()
                .username("testuser")
                .password("somepassword")
                .build();
        User user2 = User.builder()
                .username("testuser2")
                .password("somepassword")
                .build();
        entityManager.persist(user);
        entityManager.persist(user2);
        RefreshToken expiredToken = RefreshToken.builder()
                .user(user)
                .token("expired")
                .refreshExpiration(Instant.now().minusSeconds(1000))
                .build();
        RefreshToken revokedToken = RefreshToken.builder()
                .user(user2)
                .token("revoked")
                .refreshExpiration(Instant.now().plusSeconds(1000))
                .revoked(true)
                .build();
        entityManager.persist(expiredToken);
        entityManager.persist(revokedToken);

        repository.deleteExpiredAndRevokedTokens(Instant.now());

        assertThat(repository.findAll()).isEmpty();
    }
}