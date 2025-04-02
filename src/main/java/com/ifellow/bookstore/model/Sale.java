package com.ifellow.bookstore.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class Sale {
    private UUID id;
    private UUID storeId;
    private List<Book> books;
    private Date saleDate;

    Sale(UUID id, UUID storeId, List<Book> books, Date saleDate) {
        this.id = id;
        this.storeId = storeId;
        this.books = books;
        this.saleDate = saleDate;
    }

    public static SaleBuilder builder() {
        return new SaleBuilder();
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", storeId=" + storeId +
                ", books=" + books +
                ", saleDate=" + saleDate +
                '}';
    }

    public static class SaleBuilder {
        private UUID id;
        private UUID storeId;
        private List<Book> books;
        private Date saleDate;

        SaleBuilder() {
        }

        public SaleBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public SaleBuilder storeId(UUID storeId) {
            this.storeId = storeId;
            return this;
        }

        public SaleBuilder books(List<Book> books) {
            this.books = books;
            return this;
        }

        public SaleBuilder saleDate(Date saleDate) {
            this.saleDate = saleDate;
            return this;
        }

        public Sale build() {
            return new Sale(this.id, this.storeId, this.books, this.saleDate);
        }

        public String toString() {
            return "Sale.SaleBuilder(id=" + this.id + ", storeId=" + this.storeId + ", books=" + this.books + ", saleDate=" + this.saleDate + ")";
        }
    }
}
