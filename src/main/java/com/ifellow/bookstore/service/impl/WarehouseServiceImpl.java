package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.BookBulkAddDto;
import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.dto.response.WarehouseResponseDto;
import com.ifellow.bookstore.exception.BookNotFoundException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.WarehouseNotFoundException;
import com.ifellow.bookstore.mapper.WarehouseBookAmountMapper;
import com.ifellow.bookstore.mapper.WarehouseMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Warehouse;
import com.ifellow.bookstore.model.WarehouseBookAmount;
import com.ifellow.bookstore.repository.WarehouseBookAmountRepository;
import com.ifellow.bookstore.repository.WarehouseRepository;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseBookAmountRepository warehouseBookAmountRepository;
    private final WarehouseMapper warehouseMapper;
    private final BookService bookService;
    private final WarehouseBookAmountMapper warehouseBookAmountMapper;
    private final Integer EMPTY_STOCK = 0;

    @Override
    @Transactional
    public WarehouseResponseDto save(WarehouseRequestDto warehouseRequestDto) {
        Warehouse warehouse = warehouseMapper.toEntity(warehouseRequestDto);
        warehouseRepository.save(warehouse);
        return warehouseMapper.toResponseDto(warehouse);
    }

    @Override
    public Warehouse findWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found with bookId: " + id));
    }

    public void checkWarehouseExistence(Long id) {
        findWarehouseById(id);
    }

    @Override
    @Transactional
    public void addBookToWarehouse(Long id, Long bookId, int quantity) {
        if (quantity <= EMPTY_STOCK) throw new IllegalArgumentException("quantity must be greater than zero");

        Warehouse warehouse = findWarehouseById(id);
        Book book = bookService.findBookById(bookId);

        Optional<WarehouseBookAmount> optionalWba = warehouseBookAmountRepository.findByWarehouseIdAndBookId(id, bookId);
        WarehouseBookAmount warehouseBookAmount;
        if (optionalWba.isPresent()) {
            warehouseBookAmount = optionalWba.get();
            warehouseBookAmount.setAmount(warehouseBookAmount.getAmount() + quantity);
        } else {
            warehouseBookAmount = new WarehouseBookAmount();
            warehouseBookAmount.setAmount(quantity);
            warehouseBookAmount.setBook(book);
            warehouseBookAmount.setWarehouse(warehouse);

        }
        warehouseBookAmountRepository.save(warehouseBookAmount);

    }

    @Override
    @Transactional
    public void removeBookFromWarehouse(Long id, Long bookId, int quantity) {
        if (quantity <= EMPTY_STOCK) throw new IllegalArgumentException("quantity must be greater than zero");

        checkWarehouseExistence(id);
        bookService.checkBookExistence(bookId);

        Optional<WarehouseBookAmount> optionalWba = warehouseBookAmountRepository.findByWarehouseIdAndBookId(id, bookId);

        if (optionalWba.isPresent()) {
            WarehouseBookAmount warehouseBookAmount = optionalWba.get();

            if (warehouseBookAmount.getAmount() < quantity)
                throw new NotEnoughStockException("Not enough stock of book with id: " + bookId + " for removing it in warehouse with id:" + id);

            warehouseBookAmount.setAmount(warehouseBookAmount.getAmount() - quantity);
            warehouseBookAmountRepository.save(warehouseBookAmount);
        } else {
            throw new BookNotFoundException("Book not found with id: " + bookId + " in warehouse with id: " + id);
        }
    }

    @Override
    public Page<WarehouseBookResponseDto> getWarehouseStock(Long id, Pageable pageable) {
        return warehouseBookAmountRepository.findByWarehouseId(id, pageable)
                .map(warehouseBookAmountMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void bulkAddBooks(Long id, List<BookBulkAddDto> booksToAdd) {
        for (BookBulkAddDto bookDto : booksToAdd) {
            addBookToWarehouse(id, bookDto.bookId(), bookDto.quantity());
        }
    }
}
