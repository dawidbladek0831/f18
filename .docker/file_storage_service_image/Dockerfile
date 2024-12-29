FROM gradle:8.12.0-jdk21-jammy AS builder
WORKDIR /workspace/app
COPY build.gradle .
COPY settings.gradle .
COPY service/ service/
COPY common/ common/
RUN gradle service:file-storage:build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /workspace/app
EXPOSE 8080
COPY --from=builder /workspace/app/service/file-storage/build/libs/file-storage-service-executable.jar /workspace/app/file-storage-service-executable.jar

ENTRYPOINT ["java", "-jar", "file-storage-service-executable.jar"]