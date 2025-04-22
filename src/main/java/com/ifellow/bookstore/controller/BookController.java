package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.service.api.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto create(@RequestBody BookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponseDto findById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/genre/{genreId}")
    public Page<BookResponseDto> findByGenreId(@PathVariable Long genreId, Pageable pageable) {
        return bookService.findByGenreId(genreId, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/author/{authorId}")
    public Page<BookResponseDto> findByAuthorId(@PathVariable Long authorId, Pageable pageable) {
        return bookService.findByAuthorId(authorId, pageable);
    }

    @GetMapping("/title")
    @ResponseStatus(HttpStatus.OK)
    public Page<BookResponseDto> findByTitle(@RequestParam String title, Pageable pageable) {
        return bookService.findByTitle(title, pageable);
    }

    @GetMapping("/author")
    @ResponseStatus(HttpStatus.OK)
    public Page<BookResponseDto> findByAuthorFullName(@RequestParam String authorFullName, Pageable pageable) {
        return bookService.findByAuthorFullName(authorFullName, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/author-title")
    Page<BookResponseDto> findByAuthorFullNameAndTitle(@RequestParam String authorFullName, @RequestParam String title, Pageable pageable) {
        return bookService.findByAuthorFullNameAndTitle(authorFullName, title, pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/grouped-by-genre")
    Page<BookResponseDto> findAllGroupedByGenre(Pageable pageable) {
        return bookService.findAllGroupedByGenre(pageable);
    }
}
