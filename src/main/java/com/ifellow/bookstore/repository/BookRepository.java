package com.ifellow.bookstore.repository;

import com.ifellow.bookstore.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByGenreId(Long genreId, Pageable pageable);
    Page<Book> findByAuthorId(Long authorId, Pageable pageable);
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Book> findByAuthorFullNameIgnoreCase(String authorFullName, Pageable pageable);
    Page<Book> findByAuthorFullNameIgnoreCaseAndTitleContainingIgnoreCase(String authorFullName, String title, Pageable pageable);
    @Query("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.genre ORDER BY b.genre.id ASC")
    Page<Book> findAllOrderedByGenreAsc(Pageable pageable);

}
