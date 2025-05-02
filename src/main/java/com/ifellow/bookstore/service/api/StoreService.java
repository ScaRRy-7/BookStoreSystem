package com.ifellow.bookstore.service.api;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreBookResponseDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreService {

    StoreResponseDto save(StoreRequestDto storeRequestDto);
    StoreResponseDto findById(Long id);
    Store findStoreById(Long id);
    void addBookToStore(Long id, BookBulkDto bookBulkDto);
    void removeBookFromStore(Long id, BookBulkDto bookBulkDto);
    void addBooksToStore(Long id, List<BookBulkDto> bookBulkDtos);
    void removeBooksFromStore(Long id, List<BookBulkDto> bookBulkDtos);
    Page<StoreBookResponseDto> getStoreStock(Long id, Pageable pageable);
}
