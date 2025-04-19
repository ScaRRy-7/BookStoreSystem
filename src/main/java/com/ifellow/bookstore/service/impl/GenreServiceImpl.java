package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.exception.GenreNotFoundException;
import com.ifellow.bookstore.mapper.GenreMapper;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.api.GenreRepository;
import com.ifellow.bookstore.service.api.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;


    @Transactional
    public GenreResponseDto save(GenreRequestDto genreRequestDto) {
        Genre genre = genreMapper.toEntity(genreRequestDto);
        genreRepository.save(genre);
        return genreMapper.toResponseDto(genre);
    }

    @Override
    public Genre findGenreById(Long id) throws GenreNotFoundException {
        return genreRepository.findById(id).orElseThrow(
                () -> new GenreNotFoundException("Genre not found with bookId: " + id)
        );
    }

    @Override
    public GenreResponseDto findById(Long id) throws GenreNotFoundException {
        return genreRepository.findById(id)
                .map(genreMapper::toResponseDto)
                .orElseThrow(() -> new GenreNotFoundException("Genre not found with bookId: " + id));
    }
}
