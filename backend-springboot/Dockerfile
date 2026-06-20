# ---- build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- runtime ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/bjj-academy-*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=dev-h2
ENTRYPOINT ["java", "-jar", "app.jar"]
