# Step 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Production stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Optional debug: print what was created
# RUN ls -la /app/target  <-- won't work here, so do it in builder if needed

# Copy the JAR directly
COPY --from=builder /app/target/financial-guru-backend-0.0.8-SNAPSHOT.jar app.jar
# Copy your local translation.json from resources to /secrets inside container
COPY src/main/resources/translation.json /secrets/translation.json


EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
