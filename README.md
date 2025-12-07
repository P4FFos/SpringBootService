# Podcast Service

## API
- `POST /api/podcasts` створити подкаст
- `GET /api/podcasts/{id}` отримати подкаст з епізодами
- `PUT /api/podcasts/{id}` / `DELETE /api/podcasts/{id}` оновити / видалити
- `POST /api/podcasts/_list` фільтрований список (за полями `PodcastFilterRequest`)
- `POST /api/podcasts/_report` сформувати CSV зі списком подкастів
- `POST /api/podcasts/upload` імпорт колекції подкастів з JSON файлу (multipart `file`)
- `GET /api/episodes` список епізодів
- `POST /api/episodes` створити епізод (з посиланням на `podcastId`)
- `PUT /api/episodes/{id}` / `DELETE /api/episodes/{id}` оновити / видалити епізод

## Структура
- `src/main/java` код сервісу, контролери, репозиторії та специфікації
- `src/main/resources` конфігурації, Liquibase
- `src/test/java` інтеграційні та модульні тести
- `src/test/resources` тестові налаштування
