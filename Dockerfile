# Stage 1: Build using Gradle with JDK 17
FROM gradle:8.7-jdk17 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

RUN gradle --no-daemon build -x test || true

# Copy source code
COPY src ./src

# Final build (skip tests for speed)
RUN gradle --no-daemon build -x test

# Stage 2: Run the app with a lightweight JDK 17 image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
