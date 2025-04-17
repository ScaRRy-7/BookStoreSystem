package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.model.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(AuthorRequestDto authorRequestDto) {
        return Author.builder()
                .fullName(authorRequestDto.fullName())
                .build();
    }

    public AuthorResponseDto toResponseDto(Author author) {
        return new AuthorResponseDto(
                author.getId(),
                author.getFullName()
        );
    }
}
