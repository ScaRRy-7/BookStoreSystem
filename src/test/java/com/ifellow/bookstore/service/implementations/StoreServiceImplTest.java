package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.StoreDao;
import com.ifellow.bookstore.dto.request.StoreRequestDto;
import com.ifellow.bookstore.dto.response.StoreResponseDto;
import com.ifellow.bookstore.exception.StoreNotFoundException;
import com.ifellow.bookstore.model.Store;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private StoreDao storeDao;

    @InjectMocks
    private StoreServiceImpl storeService;

    @Test
    @DisplayName("StoreService ищет Store по указанному id и возвращает его")
    public void findById_CorrectArgument_ReturnStoreResponseDto() {
        UUID id = UUID.randomUUID();
        Store store = new Store(id, "Магазин книг", "Ул. Арбат");
        Mockito.when(storeDao.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(store));

        StoreResponseDto responseDto = storeService.findById(id);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(store.getName(), responseDto.name());
        Assertions.assertEquals(store.getAddress(), responseDto.address());
        Mockito.verify(storeDao, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("StoreService ищет Store по несуществующему id и выбрасывает Исключение")
    public void findById_IncorrectArgument_throwsException() {
        Mockito.when(storeDao.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(StoreNotFoundException.class, () -> storeService.findById(UUID.randomUUID()));
        Mockito.verify(storeDao, Mockito.times(1)).findById(Mockito.any(UUID.class));

    }

    @Test
    @DisplayName("StoreService добавляет Store с помощью корректного StoreRequestDto и возвращает id")
    public void add_CorrectArgument_Id() {
        StoreRequestDto storeRequestDto = new StoreRequestDto("Магазин классных книг", "Малая пироговка");

        UUID actualId = storeService.add(storeRequestDto);

        Assertions.assertNotNull(actualId);
        Assertions.assertEquals(actualId.getClass(), UUID.class);
        Mockito.verify(storeDao, Mockito.times(1)).add(Mockito.any(Store.class));

    }

    @Test
    @DisplayName("StoreService получает корректный StoreRequestDto и удаляет Store")
    public void remove_CorrectArgument_ReturnNothing() {
        StoreRequestDto storeRequestDto = new StoreRequestDto("Магазин классных книг", "Малая пироговка");

        storeService.remove(storeRequestDto);

        Mockito.verify(storeDao, Mockito.times(1)).remove(Mockito.any(Store.class));
        Assertions.assertDoesNotThrow(() -> storeService.remove(storeRequestDto));
    }

}