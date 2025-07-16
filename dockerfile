# Multi-stage build for Spring Boot application
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://ep-cool-sea-a1p9qnk3-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# Copy gradle wrapper and gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Copy source code
COPY src src
RUN apk add --no-cache ca-certificates openssl

# Copy mock certificate (ensure this file exists in the build context)
COPY tma.crt /usr/local/share/ca-certificates/tma.crt
RUN keytool -importcert -noprompt -trustcacerts -alias tma -file /usr/local/share/ca-certificates/tma.crt -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit
 
# Make gradlew executable and build the application (skip tests)
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon


# Expose port
EXPOSE 8080


# Run the application
ENTRYPOINT ["java", "-jar", "build/libs/candy-0.0.1-SNAPSHOT.jar"]
