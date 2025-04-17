package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.exception.AuthorNotFoundException;
import com.ifellow.bookstore.mapper.AuthorMapper;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.repository.api.AuthorRepository;
import com.ifellow.bookstore.service.api.AuthorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;


    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    @Transactional
    public AuthorResponseDto save(AuthorRequestDto authorRequestDto) {
        Author author = authorMapper.toEntity(authorRequestDto);
        authorRepository.save(author);
        return authorMapper.toResponseDto(author);
    }

    @Override
    public AuthorResponseDto findById(Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::toResponseDto)
                .orElseThrow(() -> new AuthorNotFoundException("Author not found with bookId: " + id));
    }

    @Override
    public Author findAuthorById(Long id) {
        return authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException("Author not found with bookId: " + id));
    }
}
