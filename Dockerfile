# Gunakan image Maven untuk build
FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app

# Copy semua file ke container dan build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Gunakan image JDK yang lebih ringan untuk run
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy JAR dari stage sebelumnya
COPY --from=build /app/target/*.jar app.jar

# Expose port default Spring Boot
EXPOSE 8080

# Jalankan aplikasi
ENTRYPOINT ["java", "-jar", "app.jar"]
