
# Step 1: Use Maven and Java 21 to build our application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Copy Java source code
COPY src ./src

# Build the Spring Boot JAR
RUN mvn -B -DskipTests package


# Step 2: Use Java to run the completed application
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
