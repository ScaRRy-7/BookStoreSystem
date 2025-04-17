package com.ifellow.bookstore.service.impl;

import com.ifellow.bookstore.dto.request.BookSaleDto;
import com.ifellow.bookstore.dto.response.SaleResponseDto;
import com.ifellow.bookstore.mapper.SaleMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Sale;
import com.ifellow.bookstore.model.SaleItem;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.repository.api.SaleItemRepository;
import com.ifellow.bookstore.repository.api.SaleRepository;
import com.ifellow.bookstore.service.api.BookService;
import com.ifellow.bookstore.service.api.SaleService;
import com.ifellow.bookstore.service.api.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final StoreService storeService;
    private final BookService bookService;
    private final SaleMapper saleMapper;

    public SaleServiceImpl(SaleRepository saleRepository,
                           StoreService storeService,
                           SaleItemRepository saleItemRepository,
                           BookService bookService,
                           SaleMapper saleMapper) {
        this.saleRepository = saleRepository;
        this.storeService = storeService;
        this.saleItemRepository = saleItemRepository;
        this.bookService = bookService;
        this.saleMapper = saleMapper;
    }

    @Override
    @Transactional
    public SaleResponseDto processSale(Long storeId, List<BookSaleDto> bookSaleDtoList) {
        Store store = storeService.findStoreById(storeId);

        Sale sale = new Sale();
        sale.setStore(store);
        sale.setSaleDateTime(LocalDateTime.now());

        for (BookSaleDto bookSaleDto : bookSaleDtoList) {
            storeService.removeBookFromStore(storeId, bookSaleDto.bookId(), bookSaleDto.quantity());

            Book book = bookService.findBookById(bookSaleDto.bookId());

            SaleItem saleItem = new SaleItem();
            saleItem.setBook(book);
            saleItem.setQuantity(bookSaleDto.quantity());
            saleItem.setPrice(book.getPrice());
            saleItem.setSale(sale);

            sale.getSaleItemList().add(saleItem);
        }

        sale.setTotalPrice(calculateTotalPrice(sale.getSaleItemList()));
        saleRepository.save(sale);

        return saleMapper.toResponseDto(sale);

    }

    @Override
    public Page<SaleResponseDto> findBySaleDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return saleRepository.findBySaleDateTimeBetween(start, end, pageable)
                .map(saleMapper::toResponseDto);
    }

    @Override
    public Page<SaleResponseDto> findByStoreId(Long storeId, Pageable pageable) {
        return saleRepository.findByStoreId(storeId, pageable)
                .map(saleMapper::toResponseDto);
    }

    @Override
    public Page<SaleResponseDto> findByStoreIdAndSaleDateTimeBetween(Long storeId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return saleRepository.findByStoreIdAndSaleDateTimeBetween(storeId, start, end, pageable)
                .map(saleMapper::toResponseDto);
    }

    private BigDecimal calculateTotalPrice(List<SaleItem> saleItemList) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (SaleItem saleItem : saleItemList) {
            BigDecimal priceOfSaleItem = saleItem.getPrice().multiply(BigDecimal.valueOf(saleItem.getQuantity()));
            totalPrice = totalPrice.add(priceOfSaleItem);
        }

        return totalPrice;
    }
}
