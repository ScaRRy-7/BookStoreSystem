package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.StoreInventoryDao;
import com.ifellow.bookstore.dto.response.BookResponseDto;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.service.interfaces.StoreInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoreInventoryServiceImpl implements StoreInventoryService {

    private final StoreInventoryDao storeInventoryDao;

    public StoreInventoryServiceImpl(StoreInventoryDao storeInventoryDao) {
        this.storeInventoryDao = storeInventoryDao;
    }

    @Override
    public List<BookResponseDto> findBooksByAuthor(String author) {
        return storeInventoryDao.findBooksByAuthor(author).stream()
                .map(BookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookResponseDto> findBooksByTitle(String title) {
        return storeInventoryDao.findBooksByTitle(title).stream()
                .map(BookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<BookResponseDto>> groupBooksByGenre() {
        return storeInventoryDao.groupBooksByGenre().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(BookMapper::toResponseDTO)
                                .collect(Collectors.toList())
                ));
    }
}
