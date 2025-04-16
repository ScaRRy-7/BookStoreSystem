package com.ifellow.bookstore.repository.api;

import com.ifellow.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByGenreId(Long genreId, Pageable pageable);
    List<Book> findByAuthorId(Long authorId);
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    List<Book> findByAuthorFullNameIgnoreCase(String authorFullName);
    List<Book> findByAuthorFullNameIgnoreCaseAndTitleContainingIgnoreCase(String authorFullName, String title);
    @Query("SELECT b FROM Book b ORDER BY b.genre.id ASC")
    Page<Book> findAllOrderedByGenreAsc(Pageable pageable);

}
