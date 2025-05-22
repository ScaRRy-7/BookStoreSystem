package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.exception.GenreException;
import com.ifellow.bookstore.mapper.GenreMapper;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.GenreRepository;
import com.ifellow.bookstore.service.api.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;


    @Override
    @Transactional
    public GenreResponseDto save(GenreRequestDto genreRequestDto) {
        Genre genre = genreMapper.toEntity(genreRequestDto);
        genreRepository.save(genre);
        return genreMapper.toDto(genre);
    }

    @Override
    @Transactional(readOnly = true)
    public Genre findGenreById(Long id) throws GenreException {
        return genreRepository.findById(id).orElseThrow(
                () -> new GenreException("Genre not found with id: " + id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponseDto findById(Long id) throws GenreException {
        return genreRepository.findById(id)
                .map(genreMapper::toDto)
                .orElseThrow(() -> new GenreException("Genre not found with id: " + id));
    }
}
