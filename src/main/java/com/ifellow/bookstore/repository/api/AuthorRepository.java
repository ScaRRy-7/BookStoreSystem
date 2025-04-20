package com.ifellow.bookstore.repository.api; //в принципе, пакет api уже не особо нужен, можно все репозитории вынести в bookstore.repository

import com.ifellow.bookstore.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
