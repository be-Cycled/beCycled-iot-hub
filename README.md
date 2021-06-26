# ⛷ beCycled IoT hub 📟

Телематический хаб для GPS | ГЛОНАСС устройств и программных трекеров.

## Протоколы

| Порт | Протокол |
| --- | --- |
| 10000 | Ping-Pong (тест) |
| 10001 | ADM (Neomatica) |

## Требования

* Java 11 и выше

* Docker

## Локальная сборка и запуск

Сборка и тесты:

```bash
$ ./gradlew clean build test check javadoc
```

Локальный запуск:

```bash
$ ./gradlew shadowJar
$ java -jar build/libs/service-with-deps.jar
```

## Сборка Docker образа

```bash
$ ./gradlew shadowJar
$ docker build -t becycled-iot-hub .
```
