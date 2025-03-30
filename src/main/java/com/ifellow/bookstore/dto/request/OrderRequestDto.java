package com.ifellow.bookstore.dto.request;

import java.util.List;
import java.util.UUID;

public record OrderRequestDto(UUID storeId, List<BookRequestDto> books) { }
