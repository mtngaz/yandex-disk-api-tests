# Автотесты REST API Яндекс.Диска

Тестовое задание: пример проекта автотестов для [REST API Яндекс.Диска](https://yandex.ru/dev/disk/rest/).

**Стек:** Java 17, [JUnit 5](https://junit.org/junit5/), [REST Assured](https://github.com/rest-assured/rest-assured)  
**API:** `https://cloud-api.yandex.net/v1/disk`

## Требования

- JDK 17+
- Maven 3.8+
- OAuth-токен приложения (не используйте личный основной аккаунт — заведите отдельное OAuth-приложение и тестовый аккаунт)

## Получение OAuth-токена

1. Создайте приложение на [oauth.yandex.ru](https://oauth.yandex.ru/) (тип: **Веб-сервисы**).
2. В правах доступа укажите:
   - `cloud_api:disk.read`
   - `cloud_api:disk.write`
   - `cloud_api:disk.info`
3. Скопируйте **Client ID** и откройте в браузере:

   ```
   https://oauth.yandex.ru/authorize?response_type=token&client_id=<ClientID>
   ```

4. Скопируйте выданный токен.

Подробнее: [Доступ к API](https://yandex.ru/dev/disk/api/concepts/quickstart-docpage/), [Полигон](https://yandex.ru/dev/disk/poligon/).

## Запуск

```bash
export YANDEX_DISK_TOKEN="ваш_токен"
mvn test
```

Без токена тесты, требующие авторизации, будут **пропущены** (skipped); тесты на `401` выполняются всегда.

```bash
# только проверки без токена
mvn test -Dtest=UnauthorizedApiTest
```

## Покрытие HTTP-методов

| Метод | Операции API | Примеры тестов |
|-------|----------------|----------------|
| **GET** | Метаинформация, данные о Диске, upload/download URL | `getDiskRoot_returns200`, `getDiskInfo_returns200`, `getUploadUrl_returns200` |
| **PUT** | Создание папки, publish/unpublish | `createFolder_returns201`, `publishFolder_returns200` |
| **POST** | Перемещение, копирование | `moveFolder_returns201`, `copyFolder_returns201` |
| **DELETE** | Удаление ресурса, очистка корзины | `deleteFolderPermanently_returns204`, `emptyTrash_returns204or202` |

Дополнительно: **PATCH** (custom_properties), сценарии **401**, проверка **public_url** после публикации.

## Структура проекта

```
src/main/java/disk/api/
  DiskApiConfig.java   — URL и конфигурация
  DiskApiClient.java   — HTTP-клиент (REST Assured)
src/test/java/disk/tests/
  BaseTest.java            — setup/teardown, очистка тестовых данных
  DiskApiTest.java         — основные сценарии
  UnauthorizedApiTest.java — тесты без OAuth-токена
```

Тестовые папки и файлы создаются с префиксом `test-*` и удаляются в `@AfterEach`.

## Документация API

- [Обзор API](https://yandex.ru/dev/disk/api/concepts/about-docpage/)
- [Метаинформация](https://yandex.ru/dev/disk/api/reference/meta-docpage/)
- [Создание папки](https://yandex.ru/dev/disk/api/reference/create-folder-docpage/)
- [Загрузка / скачивание](https://yandex.ru/dev/disk/api/reference/upload-docpage/)
