package integration.repository;

import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.model.Sale;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.model.User;
import com.ifellow.bookstore.repository.SaleRepository;
import com.ifellow.bookstore.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(classes = RootConfiguration.class)
class SaleRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SaleRepository repository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Возвращает покупки произведенные пользователем")
    void findByUserId_ReturnsPageOfSales() {
        User user = userRepository.findByUsername("client").get();
        Store store = Store.builder().address("123 Main St").build();
        entityManager.persist(store);
        Sale sale = Sale.builder()
                .user(user)
                .store(store)
                .totalPrice(BigDecimal.valueOf(100))
                .saleDateTime(LocalDateTime.now())
                .build();
        entityManager.persist(sale);

        Page<Sale> result = repository.findByUserId(user.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUser().getUsername()).isEqualTo("client");
    }
}