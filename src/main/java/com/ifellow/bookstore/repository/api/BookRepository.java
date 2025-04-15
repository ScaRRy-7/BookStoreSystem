package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
