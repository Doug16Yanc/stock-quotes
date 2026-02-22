FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

RUN groupadd -g 1001 builder && \
    useradd -u 1001 -g builder -s /bin/sh -m builder

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew && chown -R builder:builder /app

USER builder

RUN ./gradlew dependencies --no-daemon

COPY --chown=builder:builder src ./src
RUN ./gradlew bootJar -x test --no-daemon && \
    mv build/libs/*.jar build/libs/app.jar

FROM gcr.io/distroless/java21-debian12:nonroot AS runtime
COPY --from=build --chown=nonroot:nonroot /app/build/libs/app.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]