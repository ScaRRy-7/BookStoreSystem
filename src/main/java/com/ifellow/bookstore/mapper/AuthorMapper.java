package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.model.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper implements ToEntityMapper<AuthorRequestDto, Author>, ToDtoMapper<Author, AuthorResponseDto> {

    public Author toEntity(AuthorRequestDto authorRequestDto) {
        return Author.builder()
                .fullName(authorRequestDto.fullName())
                .build();
    }

    public AuthorResponseDto toDto(Author author) {
        return new AuthorResponseDto(
                author.getId(),
                author.getFullName()
        );
    }
}
