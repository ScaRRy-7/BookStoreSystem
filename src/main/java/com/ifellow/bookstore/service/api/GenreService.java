package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.model.Genre;

public interface GenreService {

    GenreResponseDto save(GenreRequestDto genreRequestDto);
    Genre findGenreById(Long id);
    GenreResponseDto findById(Long id);
}
