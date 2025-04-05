package com.ifellow.bookstore.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Receipt {
    private UUID saleId;
    private double totalAmount;
    private Date issueDate;

    @Override
    public String toString() {
        return "Receipt{" +
                "saleId=" + saleId +
                ", totalAmount=" + totalAmount +
                ", issueDate=" + issueDate +
                '}';
    }
}