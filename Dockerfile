FROM maven:3.9.5-eclipse-temurin-21-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
COPY --from=build /target/*.jar webshop.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "webshop.jar"]