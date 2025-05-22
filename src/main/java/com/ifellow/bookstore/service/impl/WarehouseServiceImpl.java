package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.WarehouseRequestDto;
import com.ifellow.bookstore.dto.response.WarehouseBookResponseDto;
import com.ifellow.bookstore.dto.response.WarehouseResponseDto;
import com.ifellow.bookstore.exception.BookException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.WarehouseException;
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
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponseDto findById(Long id) throws WarehouseException {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(
                () -> new WarehouseException("Warehouse not found with id: " + id)
        );
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse findWarehouseById(Long id) throws WarehouseException {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseException("Warehouse not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public void checkWarehouseExistence(Long id) throws WarehouseException {
        if (!warehouseRepository.existsById(id)) {
            throw new WarehouseException("Warehouse not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public void addBookToWarehouse(Long id, BookBulkDto bookBulkDto) {
        int quantity = bookBulkDto.quantity();
        Long bookId = bookBulkDto.bookId();

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
    public void removeBookFromWarehouse(Long id, BookBulkDto bookBulkDto) {
        int quantity = bookBulkDto.quantity();
        Long bookId = bookBulkDto.bookId();

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
            throw new BookException("Book not found with id: " + bookId + " in warehouse with id: " + id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseBookResponseDto> getWarehouseStock(Long id, Pageable pageable) {
        return warehouseBookAmountRepository.findByWarehouseId(id, pageable)
                .map(warehouseBookAmountMapper::toDto);
    }

    @Override
    @Transactional
    public void addBooksToWarehouse(Long id, List<BookBulkDto> bookBulkDtos) {
        for (BookBulkDto bookBulkDto : bookBulkDtos) {
            addBookToWarehouse(id, bookBulkDto);
        }
    }

    @Override
    @Transactional
    public void removeBooksFromWarehouse(Long id, List<BookBulkDto> bookBulkDtos) {
        for (BookBulkDto bookBulkDto : bookBulkDtos) {
            removeBookFromWarehouse(id, bookBulkDto);
        }
    }
}
