# Bước 1: Build ứng dụng
FROM gradle:7.6.1-jdk17 AS build

# Đặt thư mục làm việc
WORKDIR /app

# Copy file build.gradle và các file cần thiết
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

# Build ứng dụng
RUN gradle build -x test

# Bước 2: Chạy ứng dụng
FROM openjdk:17-jdk-slim

# Copy file jar từ bước build
COPY --from=build /app/build/libs/*.jar app.jar

# Expose cổng 8080
EXPOSE 8080

# Khởi động ứng dụng
ENTRYPOINT ["java", "-jar", "/app.jar"]
