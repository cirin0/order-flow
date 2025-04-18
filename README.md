# OrderFlow

Система управління замовленнями для електронної комерції, розроблена на Java з використанням Spring Framework.

## 📋 Функціональні вимоги

### Управління користувачами
- Зберігання інформації про користувачів (ім'я, прізвище, email)
- Ідентифікація користувачів за унікальною email-адресою
- Можливість оновлення профілю користувача

### Управління товарами
- Каталогізація товарів з детальною інформацією (назва, ціна, кількість на складі)
- Моніторинг кількості товарів на складі
- Автоматичне коригування запасів при створенні та скасуванні замовлень

### Кошик користувача
- Можливість додавання/видалення товарів до кошика
- Автоматичний розрахунок загальної вартості товарів у кошику
- Очищення кошика після успішного оформлення замовлення

### Створення замовлення
- Формування замовлення на основі вмісту кошика користувача
- Перевірка наявності замовлених товарів на складі
- Автоматичне оновлення запасів після підтвердження замовлення
- Генерація унікального номера для кожного замовлення
- Збереження адреси доставки у складі замовлення

### Управління замовленнями
- Перегляд списку всіх замовлень та пошук за різними параметрами
- Підтримка різних статусів для відстеження життєвого циклу замовлення: NEW, PAID, PROCESSING, SHIPPED, DELIVERED, COMPLETED, CANCELED
- Обмеження переходів між статусами згідно з бізнес-правилами
- Можливість скасування лише для замовлень у статусі NEW або PROCESSING
- Можливість завершення лише для замовлень у статусі DELIVERED
- Автоматичне повернення товарів на склад при скасуванні замовлення

### Комунікація з клієнтами
- Автоматичне надсилання підтвердження замовлення на email користувача
- Формування PDF-рахунку для кожного замовлення

## 🛠️ Технічна архітектура

Проєкт використовує:
- **Spring Boot** для створення веб-додатку
- **Spring Data JPA** для роботи з базою даних
- **Lombok** для зменшення шаблонного коду
- **Mockito** для модульного тестування
- **JUnit 5** для написання тестів
- **Асинхронну обробку** для відправлення електронних листів

## 📊 Модель даних

Основні сутності:
- **User** - користувачі системи
- **Product** - товари з інформацією про ціну та наявність
- **Order** - замовлення з інформацією про статус та загальну вартість
- **OrderItem** - елементи замовлення з кількістю та ціною
- **DeliveryAddress** - адреса доставки замовлення
- **Cart** - кошик користувача
- **CartItem** - елементи кошика

## 💼 Бізнес-логіка

Проєкт реалізує наступні бізнес-правила:
- Перевірка наявності товарів перед створенням замовлення
- Автоматичне зменшення запасів при створенні замовлення
- Повернення товарів на склад при скасуванні замовлення
- Обмеження на зміну статусу замовлення (наприклад, не можна змінити статус скасованого або завершеного замовлення)
- Генерація унікальних номерів замовлень
- Асинхронна відправка підтверджень замовлень електронною поштою

## 🔍 Якісні вимоги

### Надійність
- Забезпечення цілісності даних при обробці замовлень
- Коректна обробка помилок, зокрема при відсутності товарів на складі
- Механізми логування помилок для асинхронних операцій (відправка email)

### Продуктивність
- Обробка створення замовлення за час не більше 2 секунд
- Підтримка асинхронної обробки повідомлень для не критичних операцій

### Масштабованість
- Архітектура, що дозволяє горизонтальне масштабування для обробки збільшеного навантаження
- Оптимізація бази даних для швидкого пошуку та оновлення інформації про замовлення

### Безпека
- Захист персональних даних користувачів
- Контроль доступу до операцій з замовленнями
- Логування критичних операцій для подальшого аудиту

### Зручність використання
- Логічний і послідовний API
- Детальна інформація про помилки для розробників
- Підтримка Swagger/OpenAPI для документації API

### Тестованість
- Повне покриття модульними тестами
- Інтеграційні тести для основних бізнес-процесів
- Структурований код для легкого mock-тестування зовнішніх залежностей

### Підтримуваність
- Добре документований код
- Використання загальноприйнятих патернів проєктування
- Детальне логування для діагностики проблем

## 🧪 Тестування

Проєкт має повне покриття тестами, що забезпечує надійність та стабільність системи при подальшому розвитку та внесенні змін.

## 🚀 Запуск проєкту

```bash
# Клонування репозиторію
git clone https://github.com/cirin0/order-flow.git
cd order-flow

# Збірка проєкту
./mvnw clean install

# Запуск додатку
./mvnw spring-boot:run
```

## 📝 Ліцензія

Цей проєкт ліцензований під [MIT License](LICENSE).