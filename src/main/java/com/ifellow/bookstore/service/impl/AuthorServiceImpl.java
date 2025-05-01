package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.AuthorRequestDto;
import com.ifellow.bookstore.dto.response.AuthorResponseDto;
import com.ifellow.bookstore.exception.AuthorException;
import com.ifellow.bookstore.mapper.AuthorMapper;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.repository.AuthorRepository;
import com.ifellow.bookstore.service.api.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    @Transactional
    public AuthorResponseDto save(AuthorRequestDto authorRequestDto) {
        Author author = authorMapper.toEntity(authorRequestDto);
        authorRepository.save(author);
        return authorMapper.toDto(author);
    }

    @Override
    public AuthorResponseDto findById(Long id) throws AuthorException {
        return authorRepository.findById(id)
                .map(authorMapper::toDto)
                .orElseThrow(() -> new AuthorException("Author not found with id: " + id));
    }

    @Override
    public Author findAuthorById(Long id) throws AuthorException {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorException("Author not found with id: " + id));
    }
}
