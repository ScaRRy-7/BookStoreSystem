package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreBookResponseDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.exception.BookException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.StoreException;
import com.ifellow.bookstore.mapper.StoreBookAmountMapper;
import com.ifellow.bookstore.mapper.StoreMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.model.StoreBookAmount;
import com.ifellow.bookstore.repository.StoreBookAmountRepository;
import com.ifellow.bookstore.repository.StoreRepository;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final BookService bookService;
    private final StoreBookAmountRepository storeBookAmountRepository;
    private final StoreBookAmountMapper storeBookAmountMapper;
    private final Integer EMPTY_STOCK = 0;

    @Override
    @Transactional
    public StoreResponseDto save(StoreRequestDto storeRequestDto) {
        Store store = storeMapper.toEntity(storeRequestDto);
        storeRepository.save(store);
        return storeMapper.toDto(store);
    }

    @Override
    public StoreResponseDto findById(Long id) throws StoreException {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreException("Store not found with id " + id));
        return storeMapper.toDto(store);

    }

    @Override
    public Store findStoreById(Long id) throws StoreException {
        return storeRepository.findById(id)
                .orElseThrow(() -> new StoreException("Store not found with id: " + id));
    }


    public void checkStoreExistence(Long id) throws StoreException {
        findStoreById(id);
    }

    @Override
    @Transactional
    public void addBookToStore(Long id, BookBulkDto bookBulkDto)
            throws IllegalArgumentException, StoreException, BookException {
        int quantity = bookBulkDto.quantity();
        Long bookId = bookBulkDto.bookId();

        if (quantity <= EMPTY_STOCK) throw new IllegalArgumentException("quantity must be greater than zero");

        Store store = findStoreById(id);
        Book book = bookService.findBookById(bookId);

        Optional<StoreBookAmount> optionalSba = storeBookAmountRepository.findByStoreIdAndBookId(id, bookId);
        StoreBookAmount storeBookAmount;
        // кажется, что вместо if можно было бы воспользоваться optionalSba.orElseGet(...)
        // могу быть не права и решение с if-ом более изящное
        if (optionalSba.isPresent()) {
            storeBookAmount = optionalSba.get();
            storeBookAmount.setAmount(storeBookAmount.getAmount() + quantity);
        } else {
            storeBookAmount = new StoreBookAmount();
            storeBookAmount.setAmount(quantity);
            storeBookAmount.setBook(book);
            storeBookAmount.setStore(store);
        }

        storeBookAmountRepository.save(storeBookAmount);
    }

    @Override
    @Transactional
    public void removeBookFromStore(Long id, BookBulkDto bookBulkDto)
            throws IllegalArgumentException, NotEnoughStockException, BookException, StoreException {
        int quantity = bookBulkDto.quantity();
        Long bookId = bookBulkDto.bookId();

        // зачем тут эта проверка, если в dto уже есть проверка через Spring Validation?
        if (quantity <= EMPTY_STOCK) throw new IllegalArgumentException("quantity must be greater than zero");

        checkStoreExistence(id);
        bookService.checkBookExistence(bookId);

        storeBookAmountRepository.findByStoreIdAndBookId(id, bookId)
                .ifPresentOrElse(sba -> {
                    if (sba.getAmount() < quantity)
                        throw new NotEnoughStockException("Not enough stock of book with id: " + bookId + " for removing it in store with id:" + id);

                    sba.setAmount(sba.getAmount() - quantity);
                    storeBookAmountRepository.save(sba);
                }, () -> {
                    throw new BookException("Book not found with id: " + bookId + " in store with id: " + id);
                });
    }

    @Override
    @Transactional
    public void addBooksToStore(Long id, List<BookBulkDto> bookBulkDtos) {
        for (BookBulkDto bookBulkDto : bookBulkDtos) {
            addBookToStore(id, bookBulkDto);
        }
    }

    @Override
    @Transactional
    public void removeBooksFromStore(Long id, List<BookBulkDto> bookBulkDtos) {
        for (BookBulkDto bookBulkDto : bookBulkDtos) {
            removeBookFromStore(id, bookBulkDto);
        }
    }

    @Override
    public Page<StoreBookResponseDto> getStoreStock(Long id, Pageable pageable) {
       return storeBookAmountRepository.findByStoreId(id, pageable)
               .map(storeBookAmountMapper::toDto);
    }
}
