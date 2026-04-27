# Antivirus Expert System Java

Java Swing + SQLite експертна система для вибору антивірусу в комп'ютерній мережі.

## Як відкрити в IntelliJ IDEA

1. `File -> Open...`
2. Вибрати папку `AntivirusExpertSystemJava` або файл `build.gradle`.
3. Якщо IntelliJ запитає, обрати `Open as Project` / `Load Gradle Project`.
4. Дочекатися завантаження залежностей Gradle.
5. Запустити клас `com.example.antivirus.Main` або Gradle task `application -> run`.

## База даних

SQLite база створюється автоматично при першому запуску:

```text
data/antivirus_expert.sqlite
```

Якщо файлу БД немає або таблиці порожні, програма створює початкові дані.


## Запуск

У IntelliJ IDEA можна запустити клас `com.example.antivirus.Main` або Gradle task:

```text
Tasks -> application -> run
```

Створення JAR:

```bash
gradle clean jar
```

Після цього JAR буде у папці `build/libs`.
