package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.GenreRequestDto;
import com.ifellow.bookstore.dto.response.GenreResponseDto;
import com.ifellow.bookstore.service.api.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/genres")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MANAGER')")
    public GenreResponseDto create(@Valid @RequestBody GenreRequestDto genreRequestDto) {
        return genreService.save(genreRequestDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('CLIENT', 'MANAGER', 'ADMIN')")
    public GenreResponseDto findById(@PathVariable Long id) {
        return genreService.findById(id);
    }
}
