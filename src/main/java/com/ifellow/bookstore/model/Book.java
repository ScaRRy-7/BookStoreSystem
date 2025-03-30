package com.ifellow.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Book {
    private final String title;
    private final String author;
    private final String genre;
    private final double retailPrice;
    private final double tradePrice;
    @Setter
    private UUID storeId;

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                  ", retailPrice=" + retailPrice +
                ", tradePrice=" + tradePrice +
                ", storeId=" + storeId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Double.compare(retailPrice, book.retailPrice) == 0 &&
                Double.compare(tradePrice, book.tradePrice) == 0 &&
                Objects.equals(title, book.title) && Objects.equals(author, book.author) &&
                Objects.equals(genre, book.genre) && Objects.equals(storeId, book.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, genre, retailPrice, tradePrice, storeId);
    }
}
