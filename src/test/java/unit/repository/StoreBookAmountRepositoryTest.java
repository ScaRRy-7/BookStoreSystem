package unit.repository;

import static org.junit.jupiter.api.Assertions.*;

package com.ifellow.bookstore.repository;

import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.model.StoreBookAmount;
import com.ifellow.bookstore.repository.StoreBookAmountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StoreBookAmountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StoreBookAmountRepository repository;

    private Store store;
    private Book book;
    private StoreBookAmount storeBookAmount;

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setAddress("Test Address");
        entityManager.persist(store);

        book = new Book();
        book.setTitle("Test Book");
        book.setPrice(100);
        entityManager.persist(book);

        storeBookAmount = new StoreBookAmount();
        storeBookAmount.setStore(store);
        storeBookAmount.setBook(book);
        storeBookAmount.setAmount(10);
        entityManager.persist(storeBookAmount);
        entityManager.flush();
    }

    @Test
    void findByStoreId_ShouldReturnPageOfStoreBookAmounts() {
        Page<StoreBookAmount> result = repository.findByStoreId(store.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(store.getId(), result.getContent().get(0).getStore().getId());
    }

    @Test
    void findByStoreIdAndBookId_ShouldReturnStoreBookAmountWhenExists() {
        Optional<StoreBookAmount> result = repository.findByStoreIdAndBookId(store.getId(), book.getId());

        assertTrue(result.isPresent());
        assertEquals(store.getId(), result.get().getStore().getId());
        assertEquals(book.getId(), result.get().getBook().getId());
    }

    @Test
    void findByStoreIdAndBookId_ShouldReturnEmptyOptionalWhenNotExists() {
        Optional<StoreBookAmount> result = repository.findByStoreIdAndBookId(999L, 999L);
        assertFalse(result.isPresent());
    }

    @Test
    void existsByStoreIdAndBookId_ShouldReturnTrueWhenExists() {
        boolean exists = repository.existsByStoreIdAndBookId(store.getId(), book.getId());
        assertTrue(exists);
    }

    @Test
    void existsByStoreIdAndBookId_ShouldReturnFalseWhenNotExists() {
        boolean exists = repository.existsByStoreIdAndBookId(999L, 999L);
        assertFalse(exists);
    }
}