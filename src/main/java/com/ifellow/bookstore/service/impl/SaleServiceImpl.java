package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.BookBulkDto;
import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.filter.SaleFilter;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.exception.BookException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.SaleException;
import com.ifellow.bookstore.exception.StoreException;
import com.ifellow.bookstore.mapper.SaleMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Sale;
import com.ifellow.bookstore.model.SaleItem;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.repository.SaleRepository;
import com.ifellow.bookstore.service.api.*;
import com.ifellow.bookstore.specification.SaleSpecification;
import com.ifellow.bookstore.util.SaleUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;

    private final StoreService storeService;
    private final BookService bookService;
    private final AuthenticationService authenticationService;

    private final SaleMapper saleMapper;

    private final SaleUtils saleUtils;

    @Override
    @Transactional
    public SaleResponseDto processSale(Long storeId, List<BookSaleDto> bookSaleDtoList)
            throws StoreException, BookException, NotEnoughStockException {

        Store store = storeService.findStoreById(storeId);

        Sale sale = new Sale();
        sale.setStore(store);
        sale.setSaleDateTime(LocalDateTime.now());

        for (BookSaleDto bookSaleDto : bookSaleDtoList) {
            storeService.removeBookFromStore(storeId, new BookBulkDto(bookSaleDto.bookId(), bookSaleDto.quantity()));

            Book book = bookService.findBookById(bookSaleDto.bookId());

            SaleItem saleItem = new SaleItem();
            saleItem.setBook(book);
            saleItem.setQuantity(bookSaleDto.quantity());
            saleItem.setPrice(book.getPrice());
            saleItem.setSale(sale);

            sale.getSaleItemList().add(saleItem);
        }

        sale.setTotalPrice(saleUtils.calculateTotalPrice(sale.getSaleItemList()));
        sale.setUser(authenticationService.getUserInCurrentContext());
        saleRepository.save(sale);

        return saleMapper.toDto(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDto findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleException("Sale not found with id: " + id));
        return saleMapper.toDto(sale);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponseDto> findAll(SaleFilter filter, Pageable pageable) {
        Specification<Sale> spec = SaleSpecification.withFilter(filter);
        return saleRepository.findAll(spec, pageable).map(saleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponseDto> findByUserId(Long userId, Pageable pageable) {
        return saleRepository.findByUserId(userId, pageable).map(saleMapper::toDto);
    }
}
