
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Variables de entorno por defecto (pueden sobreescribirse desde docker-compose)
#ENV SPRING_PROFILES_ACTIVE=default
#ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/microservicio_db
#ENV SPRING_DATASOURCE_USERNAME=admin
#ENV SPRING_DATASOURCE_PASSWORD=admin123
#ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

