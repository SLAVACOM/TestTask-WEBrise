# TestTask-WEBrise

## Описание

Этот проект предназначен для демонстрации выполнения тестового задания. Он реализован с использованием современных технологий и поддерживает запуск как напрямую из среды разработки IntelliJ IDEA, так и с помощью контейнеризации через Docker.

---

## Технологический стек

- **Java** — основной язык программирования бэкенда
- **Spring Boot** — фреймворк для создания REST API и управления зависимостями
- **Maven** — система управления зависимостями и сборки проекта
- **PostgreSQL** — реляционная база данных (или другая, если указано в настройках)
- **Docker** — контейнеризация и удобный деплой
- **Lombok** — аннотации для сокращения шаблонного кода
- **Spring Data JPA** — работа с базой данных через ORM
- **Slf4j** — логирование в консоль и сохранение в отдельный файл как всех логов, так и логов от EndPoint`ов API в отдельный файл

---

## Варианты запуска

### 1. Запуск через IntelliJ IDEA

1. **Клонируйте репозиторий:**
   ```bash
   git clone https://github.com/SLAVACOM/TestTask-WEBrise.git
   cd TestTask-WEBrise
   ```

2. **Откройте директорию проекта в IntelliJ IDEA.**

3. **Убедитесь, что установлены JDK 17+ и Maven.**

4. **Настройте файл `application.properties` в папке `src/main/resources` (укажите параметры вашей базы данных).**

5. **Запустите проект:**
   - Через пункт меню: `Run > Run 'TestTaskApplication'`
   - Или кликнув правой кнопкой мыши по главному классу (обычно находится по пути `src/main/java/.../TestTaskApplication.java`) и выбрав `Run`.

6. **API будет доступен по адресу:**  
   ```
   http://localhost:8080/
   ```

### 2. Запуск через Docker Compose (локальная сборка)

> Docker Compose автоматически поднимет и приложение, и базу данных в отдельных контейнерах, свяжет их между собой и обеспечит корректную работу без дополнительной настройки.

1. **Убедитесь, что у вас установлены Docker и Docker Compose.**

2. **Запустите команду:**
   ```bash
   docker compose up --build
   ```
   или (если ваша версия требует дефиса)
   ```bash
   docker-compose up --build
   ```

3. **API будет доступен по адресу:**  
   ```
   http://localhost:8080/
   ```

4. **PostgreSQL будет доступен внутри сети Docker по адресу:**  
   ```
   db:5432
   ```
   (Внешний доступ обычно не требуется — параметры уже прописаны в Compose и application.properties)

---
### 3. Запуск через Docker Compose с образом из Docker Hub

>Используется готовый образ из Docker Hub, что позволяет не собирать проект вручную.

1. **Убедитесь, что у вас установлены Docker и Docker Compose.**

2. Создайте файл `docker-compose.remote.yml` следующего содержания:

```
services:
  app:
    image: slavacom/test-task-webrise:latest
    container_name: test-task-app
    restart: always
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/userservice
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_JPA_SHOW_SQL: "true"
      SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL: "true"
    networks:
      - backend

  db:
    image: postgres:15
    container_name: postgres-db
    restart: always
    environment:
      POSTGRES_DB: userservice
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    volumes:
      - pgdata:/var/lib/postgresql/data
    networks:
      - backend
volumes:
  pgdata:

networks:
  backend:
```

3. **Запустите приложение:**
   ```bash
   docker compose up
   ```
   или 
   ```bash
   docker-compose up
   ```
4. **API будет доступен по адресу:**  
   ```
   http://localhost:8080/
   ```


Для удобства работы с API можно воспользоваться 
- [Коллекция Postman (JSON)](./TestTask.postman_collection.json)
- [Коллекция Insomnia (YAML)](./Insomnia.yaml)

## Контакты

- Автор: [SLAVACOM](https://github.com/SLAVACOM)
- Вопросы и предложения — через Issues репозитория.




