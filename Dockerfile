# Используем образ Maven
FROM maven:3.8.5-openjdk-17-slim AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл pom.xml и директорию src
COPY pom.xml .
COPY src ./src

# Загружаем зависимости и собираем проект
RUN mvn clean package -DskipTests

# Используем образ OpenJDK для запуска приложения
FROM openjdk:17-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем скомпилированный jar файл
COPY --from=build /app/target/ChatappApi-0.0.1-SNAPSHOT.jar app.jar

# Запускаем приложение
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]