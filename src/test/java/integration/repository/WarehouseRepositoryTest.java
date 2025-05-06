package integration.repository;

import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.model.Warehouse;
import com.ifellow.bookstore.repository.WarehouseRepository;
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
class WarehouseRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private WarehouseRepository repository;

    @Test
    @DisplayName("Сохраняет склад и возвращает по id")
    void saveAndFindById_ReturnsWarehouse() {
        Warehouse warehouse = Warehouse.builder().address("Склад").build();
        entityManager.persist(warehouse);
        entityManager.flush();

        Optional<Warehouse> result = repository.findById(warehouse.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAddress()).isEqualTo("Склад");
    }
}