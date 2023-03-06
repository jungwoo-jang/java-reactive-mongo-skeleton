# Step: Build
FROM openjdk:17.0.2-jdk-slim-buster AS builder

WORKDIR /app
COPY gradlew build.gradle.kts settings.gradle.kts gradle.properties lombok.config ./
COPY gradle ./gradle
COPY src/main ./src/main
RUN ./gradlew bootJar

# Step: Run
FROM openjdk:17.0.2-slim-buster

WORKDIR /app
COPY --from=builder /app/build/libs/java-mongo-skeleton-*.jar app.jar
COPY --from=builder /app/build/libs/dd-java-agent-*.jar dd-java-agent.jar

ENTRYPOINT ["java", "-javaagent:dd-java-agent.jar", "-jar", "app.jar"]
