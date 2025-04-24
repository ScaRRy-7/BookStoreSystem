package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.BookFilter;
import com.ifellow.bookstore.dto.request.BookRequestDto;
import com.ifellow.bookstore.dto.request.GroupedBookResponse;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.exception.BookNotFoundException;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.model.Author;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Genre;
import com.ifellow.bookstore.repository.BookRepository;
import com.ifellow.bookstore.service.api.AuthorService;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.GenreService;
import com.ifellow.bookstore.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return bookMapper.toDto(book);
    }

    @Override
    public BookResponseDto findById(Long id) throws BookNotFoundException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with bookId: " + id));

        return bookMapper.toDto(book);
   }

   public void checkBookExistence(Long id) throws BookNotFoundException {
        findById(id);
   }

   public Object findAll(BookFilter filter, Pageable pageable) {
        if (filter.getGroupByGenre()) {
            return findAllGroupedByGenre(filter, pageable);
        } else {
            return findAllFiltered(filter, pageable);
        }
   }

   private Page<BookResponseDto> findAllFiltered(BookFilter filter, Pageable pageable) {
       Specification<Book> spec = BookSpecification.withFilter(filter);
       return bookRepository.findAll(spec, pageable)
               .map(bookMapper::toDto);
   }

   private GroupedBookResponse findAllGroupedByGenre(BookFilter filter, Pageable pageable) {
        Specification<Book> spec = BookSpecification.withFilter(filter);

        Page<Book> bookPage = bookRepository.findAll(spec, pageable);

        Map<String, List<BookResponseDto>> groupedBooks = bookPage.getContent().stream()
                .collect(Collectors.groupingBy(
                        book -> book.getGenre().getName(),
                        Collectors.mapping(bookMapper::toDto, Collectors.toList())
                ));

        return GroupedBookResponse.builder()
                .booksByGenre(groupedBooks)
                .currentPage(bookPage.getNumber())
                .totalPages(bookPage.getTotalPages())
                .totalElements(bookPage.getTotalElements())
                .build();
   }

   @Override
   public Book findBookById(Long id) throws BookNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with bookId: " + id));
   }
}
