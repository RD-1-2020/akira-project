FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

# Создаём пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем JAR файл
COPY --chown=spring:spring build/libs/akira-*.jar app.jar

# Открываем порт
EXPOSE 8443

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]

