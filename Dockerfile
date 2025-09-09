FROM gradle:8-jdk17 AS builder
WORKDIR /home/gradle/project

COPY . .

RUN gradle bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /home/gradle/project/applications/app-service/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]