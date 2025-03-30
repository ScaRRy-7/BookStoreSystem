package com.ifellow.bookstore.dto.response;

import java.util.Date;

public record ReceiptResponseDto(
        Date getIssueDate,
        double totalAmount
) {}