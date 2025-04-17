package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.model.Author;

public interface AuthorService {

    AuthorResponseDto save(AuthorRequestDto authorRequestDto);
    AuthorResponseDto findById(Long id);
    Author findAuthorById(Long id);
}
