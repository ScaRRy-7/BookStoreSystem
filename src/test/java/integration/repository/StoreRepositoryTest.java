package integration.repository;

import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.repository.StoreRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(classes = {RootConfiguration.class})
class StoreRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private StoreRepository repository;

    @Test
    @DisplayName("findByAddress находит магазин")
    void findByAddressReturnsStore() {
        Store store = Store.builder().address("Ул. Арбат").build();
        entityManager.persist(store);

        Optional<Store> result = repository.findByAddress("Ул. Арбат");

        assertThat(result).isPresent();
        assertThat(result.get().getAddress()).isEqualTo("Ул. Арбат");
    }
}