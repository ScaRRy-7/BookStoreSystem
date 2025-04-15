package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
