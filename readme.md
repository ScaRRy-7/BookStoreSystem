# 📚 Книжный магазин (BookStoreSystem)

Система управления книжным магазином на чистой Java. Проект реализует базовую логику работы
склада, физических магазинов и онлайн-заказов.

### ⚙️ Технологии
- Java 22
- Maven
- Lombok 
- Junit 5
- Mockito

### 🚀 Соберите проект с помощью Maven
```bash
mvn clean install
```

### 📄 Подключите зависимость в ваш проект
```xml
<dependency>
    <groupId>com.ifellow</groupId>
    <artifactId>BookStoreSystem</artifactId>
    <version>1.0.0</version>
</dependency>
```
## 📚Архитектура

Проект построен с использованием многослойной архитектуры, включающей DAO, сервисы, модели и DTO.
Все данные хранятся в памяти с помощью класса DataSource - для имитации базы данных.
Сервисный слой покрыт модульными тестами, покрывающими положительные и негативные сценарии.

### Сервисный слой
- OrderService - взаимодействие с заказами (отмена, выполнение, получение статуса)
- RetailSaleService - выполнение розничных продаж в магазине
- StoreInventoryService - взаимодействие с хранилищем магазина
- StoreService - управление магазинами (создание, удаление, поиск по id)
- TransferService - перевозка товара со склада в магазин
- WarehouseInventoryService - Управление хранилищем склада

### DAO (Data Access Objects)
- OrderDao - доступ к заказам
- WarehouseInventoryDao - доступ к книгам склада
- StoreInventoryDao - доступ к книгам конкретного магазина, указанного по id
- SaleDao - доступ к розничным продажам книг в магазинах
- ReceiptDao - доступ к чекам
- StoreDao - доступ к магазинам

### Модели
- Book - Книга
- Order - Заказ
- Receipt - Чек
- Sale - Продажа
- Store - Магазин

## 🌟 Функциональность
Система предоставляет следующие возможности

## Управление складом
- Добавление и удаление книг.
- Оптовая поставка книг на склад.
- Просмотр текущего ассортимента.
- Группировка и фильтрация книг по жанру или автору.
- Поиск книг по названию или автору.

## Управление магазинами
- Создание, удаление и поиск магазинов.
- Передача книг со склада в конкретный магазин.
- Розничные продажи с формированием чеков.

## Онлайн-заказы
- Создание онлайн-заказов с проверкой наличия книг.
- Отмена заказов.
- Получение статуса заказа.

## 🌟Ниже приведен подробный шаблон использования библиотеки BookStoreSystem
```java
public class BookStoreClient {
    public static void main(String[] args) {
        DataSource dataSource = DataSource.getInstance();
    
        // Инициализация DAO
        StoreDao storeDao = new StoreDaoImpl(dataSource);
        WarehouseInventoryDao warehouseDao = new WarehouseInventoryDaoImpl(dataSource);
        OrderDao orderDao = new OrderDaoImpl(dataSource);
        StoreInventoryDao storeInventoryDao = new StoreInventoryDaoImpl(dataSource);
        SaleDao saleDao = new SaleDaoImpl(dataSource);
        ReceiptDao receiptDao = new ReceiptDaoImpl(dataSource);

        // Инициализация сервисов
        StoreService storeService = new StoreServiceImpl(storeDao);
        WarehouseInventoryService warehouseService = new WarehouseInventoryServiceImpl(warehouseDao);
        TransferService transferService = new TransferServiceImpl(warehouseService, storeService);
        OrderService orderService = new OrderServiceImpl(storeService, orderDao, storeInventoryDao);
        RetailSaleServiceImpl retailSaleService = new RetailSaleServiceImpl(
                storeInventoryDao, storeService, warehouseService, saleDao, receiptDao
        );

        try {
            // Создание магазина
            UUID storeId = storeService.add(new StoreRequestDto("Главный магазин", "Москва"));
            System.out.println("Создан магазин с bookId: " + storeId);

            // Проверка существования магазина
            StoreResponseDto store = storeService.findById(storeId);
            System.out.println("Название магазина: " + store.name());

            // Оптовая поставка на склад
            List<BookRequestDto> initialStock = List.of(
                    new BookRequestDto("1984", "Оруэлл", "Антиутопия", 800.0, 500.0),
                    new BookRequestDto("1984", "Оруэлл", "Антиутопия", 800.0, 500.0),
                    new BookRequestDto("Мастер и Маргарита", "Булгаков", "Роман", 950.0, 600.0),
                    new BookRequestDto("Преступление и наказание", "Достоевский", "Роман", 850.0, 550.0),
                    new BookRequestDto("Собачье сердце", "Булгаков", "Повесть", 750.0, 450.0)
            );
            warehouseService.wholesaleBookDelivery(initialStock);
            System.out.println("Доставка на склад завершена");

            // Тест группировки по жанру на складе
            System.out.println("Книги сгруппированы по жанру на складе:");
            Map<String, List<BookResponseDto>> booksByGenre = warehouseService.groupBooksByGenre();
            booksByGenre.forEach((genre, books) -> {
                System.out.println("Жанр: " + genre);
                books.forEach(book -> System.out.println("    - " + book.title() + " Автор: " + book.author()));
            });

            // Передача книг в магазин
            transferService.transferToStore(storeId, List.of(
                    new BookRequestDto("1984", "Оруэлл", "Антиутопия", 800.0, 500.0),
                    new BookRequestDto("Мастер и Маргарита", "Булгаков", "Роман", 950.0, 600.0),
                    new BookRequestDto("Собачье сердце", "Булгаков", "Повесть", 750.0, 450.0)
            ));
            System.out.println("3 книги перевезены в магазин");

            // Создание онлайн-заказа
            OrderRequestDto orderRequest = new OrderRequestDto(
                    storeId,
                    List.of(
                            new BookRequestDto("1984", "Оруэлл", "Антиутопия", 800.0, 500.0),
                            new BookRequestDto("Мастер и Маргарита", "Булгаков", "Роман", 950.0, 600.0)
                    )
            );
            OrderResponseDto order = orderService.createOrder(orderRequest);
            System.out.println("Создан заказ: " + order.id());
            System.out.println("Стоимость заказа: " + order.totalAmount());
            System.out.println("Статус заказа: " + order.orderStatus());

            // Розничная продажа
            ReceiptResponseDto receipt = retailSaleService.processSale(
                    storeId,
                    List.of(new BookRequestDto("1984", "Оруэлл", "Антиутопия", 800.0, 500.0))
            );
            System.out.println("Розничная продажа совершена");
            System.out.println("Чек: " + receipt);
            System.out.println("Сумма на чеке: " + receipt.totalAmount());

            // Попытка отменить заказ
            orderService.cancelOrder(order.id());
            System.out.println("Попытка отменить заказ");
            OrderResponseDto updatedOrder = orderService.getOrder(order.id());
            System.out.println("Новый статус заказа: " + updatedOrder.orderStatus());

            // Фильтрация по автору на складе
            System.out.println("Книги автора Оруэлл на складе:");
            List<BookResponseDto> orwellBooks = warehouseService.findBooksByAuthor("Оруэлл");
            orwellBooks.forEach(book ->
                    System.out.println("  - " + book.title() + " (" + book.genre() + ")")
            );

        } catch (StoreNotFoundException | NotEnoughStockException | OrderNotFoundException e) {
            System.err.println("ИСКЛЮЧЕНИЕ: " + e.getMessage());
        }
    }
}
```

## ✨ Автор 
**Мерченко Семён**  
