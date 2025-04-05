package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.StoreDao;
import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.exception.StoreNotFoundException;
import com.ifellow.bookstore.mapper.StoreMapper;
import com.ifellow.bookstore.model.Store;
import com.ifellow.bookstore.service.interfaces.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreDao storeDao;

    public StoreServiceImpl(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    @Override
    public UUID add(StoreRequestDto storeRequestDto) {
        Store store = StoreMapper.toModel(storeRequestDto);
        storeDao.add(store);
        return store.getId();
    }

    @Override
    public void remove(StoreRequestDto storeRequestDto) {
        storeDao.remove(StoreMapper.toModel(storeRequestDto));
    }

    @Override
    public StoreResponseDto findById(UUID id) throws StoreNotFoundException {
        Store store = storeDao.findById(id).orElseThrow(() -> new StoreNotFoundException("Store not found with id: " + id));
        return StoreMapper.toResponseDto(store);
    }
}
