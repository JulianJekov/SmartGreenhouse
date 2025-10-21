FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY greenhouse/pom.xml .
COPY greenhouse/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]