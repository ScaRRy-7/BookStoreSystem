package com.ifellow.bookstore.service.implementations;

import com.ifellow.bookstore.dao.interfaces.OrderDao;
import com.ifellow.bookstore.dao.interfaces.StoreDao;
import com.ifellow.bookstore.dao.interfaces.StoreInventoryDao;
import com.ifellow.bookstore.dto.request.OrderRequestDto;
import com.ifellow.bookstore.dto.response.OrderResponseDto;
import com.ifellow.bookstore.enumeration.OrderStatus;
import com.ifellow.bookstore.exception.ChangeOrderStatusException;
import com.ifellow.bookstore.exception.NotEnoughStockException;
import com.ifellow.bookstore.exception.OrderNotFoundException;
import com.ifellow.bookstore.exception.StoreNotFoundException;
import com.ifellow.bookstore.mapper.BookMapper;
import com.ifellow.bookstore.mapper.OrderMapper;
import com.ifellow.bookstore.model.Book;
import com.ifellow.bookstore.model.Order;
import com.ifellow.bookstore.service.interfaces.OrderService;
import com.ifellow.bookstore.service.interfaces.StoreService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;
    private final StoreInventoryDao storeInventoryDao;
    private final StoreDao storeDao;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto)
            throws StoreNotFoundException, NotEnoughStockException {

        storeDao.findById(orderRequestDto.storeId())
                .orElseThrow(() -> new StoreNotFoundException("Store not found with id: " + orderRequestDto.storeId()));

        List<Book> requestedBooks = orderRequestDto.books().stream()
                .map(BookMapper::toModel)
                .peek(book -> book.setStoreId(orderRequestDto.storeId()))
                .toList();

        checkStockAvailability(orderRequestDto.storeId(), requestedBooks);

        Order order = new Order(
                orderRequestDto.storeId(),
                requestedBooks,
                calculateAmount(requestedBooks));

        orderDao.save(order);

        return OrderMapper.toResponseDto(order);
    }

    private void checkStockAvailability(UUID storeId, List<Book> requestedBooks) throws NotEnoughStockException {

        Map<Book, Long> requiredCounts = requestedBooks.stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

        for (Map.Entry<Book, Long> entry : requiredCounts.entrySet()) {
            Book bookType = entry.getKey();
            Long requiredCount = entry.getValue();

            List<Book> availableBooks = storeInventoryDao.findBooksByType(storeId, bookType);
            if (availableBooks.size() < requiredCount) {
                throw new NotEnoughStockException("Not enough stock in store with id: " + storeId);
            }
        }
    }

    @Override
    public void cancelOrder(UUID orderId) throws OrderNotFoundException, ChangeOrderStatusException {
        OrderStatus orderStatus = getStatusByOrderId(orderId);

        if (orderStatus != OrderStatus.CREATED)
            throw new ChangeOrderStatusException("Can't cancel order because it has status: " + orderStatus);

        orderDao.updateStatus(orderId, OrderStatus.CANCELED);
    }

    @Override
    public OrderResponseDto getOrder(UUID orderId) throws OrderNotFoundException {
        Order order = orderDao
                .findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        return OrderMapper.toResponseDto(order);
    }

    private double calculateAmount(List<Book> books) {
        return books.stream().mapToDouble(Book::getRetailPrice).sum();
    }

    public OrderStatus getStatusByOrderId(UUID orderId) throws OrderNotFoundException {
        return orderDao.getStatusByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    public void completeOrder(UUID orderId) throws OrderNotFoundException, ChangeOrderStatusException {
        Order order = orderDao.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.CREATED)
            throw new ChangeOrderStatusException("Can't complete order because it has status: " + order.getStatus());

        storeInventoryDao.removeBooks(order.getStoreId(), order.getBooks());
        orderDao.updateStatus(orderId, OrderStatus.COMPLETED);
    }
}
