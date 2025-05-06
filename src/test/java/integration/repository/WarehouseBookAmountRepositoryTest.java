package integration.repository;

import com.ifellow.bookstore.configuration.RootConfiguration;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Warehouse;
import com.ifellow.bookstore.model.WarehouseBookAmount;
import com.ifellow.bookstore.repository.WarehouseBookAmountRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(classes = RootConfiguration.class)
class WarehouseBookAmountRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private WarehouseBookAmountRepository repository;

    @Test
    @DisplayName("findByWarehouseId возвращает количество страниц")
    void findByWarehouseId_ReturnsPageOfBooks() {
        Warehouse warehouse = Warehouse.builder().address("Warehouse 1").build();
        entityManager.persist(warehouse);
        Book book = Book.builder()
                .title("Книга")
                .price(BigDecimal.ONE)
                .build();
        entityManager.persist(book);
        WarehouseBookAmount amount = WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build();
        entityManager.persist(amount);

        Page<WarehouseBookAmount> result = repository.findByWarehouseId(warehouse.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBook().getTitle()).isEqualTo("Книга");
        assertThat(result.getContent().get(0).getAmount()).isEqualTo(10);
    }

    @Test
    @DisplayName("findByWarehouseIdAndBookId возвращает количество")
    void findByWarehouseIdAndBookId_ReturnsAmount() {
        Warehouse warehouse = Warehouse.builder().address("Warehouse 1").build();
        entityManager.persist(warehouse);
        Book book = Book.builder()
                .title("Книга")
                .price(BigDecimal.ONE)
                .build();
        entityManager.persist(book);
        WarehouseBookAmount amount = WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build();
        entityManager.persist(amount);
        Optional<WarehouseBookAmount> result = repository.findByWarehouseIdAndBookId(warehouse.getId(), book.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualTo(10);
    }

    @Test
    @DisplayName("existsByWarehouseIdAndBookId возвращает true")
    void existsByWarehouseIdAndBookIdExists_ReturnsTrue() {
        Warehouse warehouse = Warehouse.builder().address("Warehouse 1").build();
        entityManager.persist(warehouse);
        Book book = Book.builder()
                .title("Книга")
                .price(BigDecimal.ONE)
                .build();
        entityManager.persist(book);
        WarehouseBookAmount amount = WarehouseBookAmount.builder()
                .warehouse(warehouse)
                .book(book)
                .amount(10)
                .build();
        entityManager.persist(amount);

        boolean exists = repository.existsByWarehouseIdAndBookId(warehouse.getId(), book.getId());

        assertThat(exists).isTrue();
    }
}