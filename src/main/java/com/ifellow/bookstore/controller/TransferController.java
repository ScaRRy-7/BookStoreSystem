package com.ifellow.bookstore.controller;

import com.ifellow.bookstore.dto.request.TransferRequestDto;
import com.ifellow.bookstore.service.api.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/fromwarehousetostore")
    public void transferBookFromWarehouseToStore(@Valid @RequestBody TransferRequestDto transferRequestDto) {
        transferService.transferBookFromWarehouseToStore(
                transferRequestDto.sourceId(),
                transferRequestDto.destinationId(),
                transferRequestDto.bookBulkDto().bookId(),
                transferRequestDto.bookBulkDto().quantity()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/fromstoretostore")
    public void transferBookFromStoreToStore(@Valid @RequestBody TransferRequestDto transferRequestDto) {
        transferService.transferBookFromStoreToStore(
                transferRequestDto.sourceId(),
                transferRequestDto.destinationId(),
                transferRequestDto.bookBulkDto().bookId(),
                transferRequestDto.bookBulkDto().quantity()
        );
    }
}
