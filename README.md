# AncapWars

Аддон на войны для AncapStates.

## API

Публичное API очень ограничено.

- `WarState.of(type, id)` для получения точки доступа к методом управления военными данными государств
- Статичные методы в `AncapWars` для глобального взаимодействия

API событий отсутствует.

## Сборка

Требования:

- Установленный Maven 3
- Локально установленный AncapFramework ветки lts/pre-v1.7-pre.10 требуемой версии из этой ветки
- Локально установленный AncapStates
- JDK 21

Процесс:

- `mvn clean install`
- Плагин будет в /target/

## Лицензия

Лицензия и причины её выбора описаны в репозитории AncapStates: [https://github.com/PukPukov/AncapStates-2](https://github.com/PukPukov/AncapStates-2)