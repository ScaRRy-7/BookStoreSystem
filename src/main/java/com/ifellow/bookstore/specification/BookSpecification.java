package com.ifellow.bookstore.specification;

import com.ifellow.bookstore.dto.request.BookFilter;
import com.ifellow.bookstore.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

public class BookSpecification {

    public static Specification<Book> withFilter(BookFilter filter) {
        return Specification.where(byAuthorId(filter.getAuthorId()))
                .and(byGenreId(filter.getGenreId()))
                .and(byTitle(filter.getTitle()))
                .and(byAuthorFullName(filter.getAuthorFullName()))
                .and(byPriceBetween(filter.getMinPrice(), filter.getMaxPrice()));
    }

    private static Specification<Book> byAuthorId(Long authorId) {
        if (authorId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    private static Specification<Book> byGenreId(Long genreId) {
        if (genreId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("genre").get("id"), genreId);
    }

    private static Specification<Book> byTitle(String title) {
        if (!StringUtils.hasText(title)) return null;
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    private static Specification<Book> byAuthorFullName(String authorFullName) {
        if (!StringUtils.hasText(authorFullName)) return null;
        return (root, query, cb) -> cb.like(cb.lower(root.get("author").get("fullname")), "%" + authorFullName + "%");
    }

    private static Specification<Book> byPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null && maxPrice == null) return null;
        return (root, query, cb) -> {
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThan(root.get("price"), minPrice);
            } else {
                return cb.lessThan(root.get("price"), maxPrice);
            }
        };
    }
}
