package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.response.ReceiptResponseDto;
import com.ifellow.bookstore.model.Receipt;

public class ReceiptMapper {

    public static ReceiptResponseDto toReceiptResponseDto(Receipt receipt) {
        return new ReceiptResponseDto(
                receipt.getIssueDate(),
                receipt.getTotalAmount()
        );
    }
}
