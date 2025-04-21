package com.ifellow.bookstore.mapper;

import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.model.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper implements ToEntityMapper<GenreRequestDto, Genre>, ToDtoMapper<Genre, GenreResponseDto> {

    public Genre toEntity(GenreRequestDto genreRequestDto) {
        return Genre.builder()
                .name(genreRequestDto.name())
                .build();
    }

    public GenreResponseDto toDto(Genre genre) {
        return new GenreResponseDto(genre.getId(), genre.getName());
    }
}
