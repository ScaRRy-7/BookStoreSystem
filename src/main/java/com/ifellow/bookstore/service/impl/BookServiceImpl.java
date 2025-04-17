package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.exception.BookNotFoundException;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.api.BookRepository;
import com.ifellow.bookstore.service.api.AuthorService;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor

public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public BookResponseDto save(BookRequestDto bookRequestDto) {
        Author author = authorService.findAuthorById(bookRequestDto.authorId());
        Genre genre = genreService.findGenreById(bookRequestDto.genreId());

        Book book = bookMapper.toEntity(bookRequestDto);
        book.setAuthor(author);
        book.setGenre(genre);

        bookRepository.save(book);
        return bookMapper.toResponseDto(book);
    }

    @Override
    public BookResponseDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with bookId: " + id));

        return bookMapper.toResponseDto(book);
   }

   @Override
   public Page<BookResponseDto> findByGenreId(Long genreId, Pageable pageable) {
        return bookRepository.findByGenreId(genreId, pageable)
                .map(bookMapper::toResponseDto);

   }

   @Override
   public Page<BookResponseDto> findByAuthorId(Long authorId, Pageable pageable) {
        return bookRepository.findByAuthorId(authorId, pageable)
                .map(bookMapper::toResponseDto);
   }

   @Override
   public Page<BookResponseDto> findByTitle(String title, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(bookMapper::toResponseDto);
   }

   @Override
   public Page<BookResponseDto> findByAuthorFullName(String fullName, Pageable pageable) {
        return bookRepository.findByAuthorFullNameIgnoreCase(fullName, pageable)
                .map(bookMapper::toResponseDto);
   }

   @Override
   public Page<BookResponseDto> findByAuthorFullNameAndTitle(String fullName, String title, Pageable pageable) {
        return bookRepository.findByAuthorFullNameIgnoreCaseAndTitleContainingIgnoreCase(fullName, title, pageable)
                .map(bookMapper::toResponseDto);
   }

   @Override
   public Page<BookResponseDto> findAllGroupedByGenre(Pageable pageable) {
        return bookRepository.findAllOrderedByGenreAsc(pageable)
                .map(bookMapper::toResponseDto);
   }

   @Override
   public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with bookId: " + id));
   }
}
