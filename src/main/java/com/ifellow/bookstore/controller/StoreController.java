package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreBookResponseDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.service.api.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreResponseDto create(@Valid @RequestBody StoreRequestDto storeRequestDto) {
        return storeService.save(storeRequestDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StoreResponseDto findById(@PathVariable Long id) {
        return storeService.findById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/stock/add")
    public void addBooksToStore(@PathVariable Long id, @Valid @RequestBody BookBulkDto bookBulkDto) {
        storeService.addBookToStore(id, bookBulkDto.bookId(), bookBulkDto.quantity());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/stock/remove")
    public void deleteBooksFromStore(@PathVariable Long id, @Valid @RequestBody BookBulkDto bookBulkDto) {
        storeService.removeBookFromStore(id, bookBulkDto.bookId(), bookBulkDto.quantity());
    }

    @GetMapping("/{id}/stock")
    @ResponseStatus(HttpStatus.OK)
    public Page<StoreBookResponseDto> getStoreStock(@PathVariable Long id, Pageable pageable) {
        return storeService.getStoreStock(id, pageable);
    }
}
