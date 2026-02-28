FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S fusion && adduser -S fusion -G fusion

COPY target/fusion-backend-*.jar app.jar

RUN chown fusion:fusion app.jar

USER fusion

EXPOSE 8080 9119

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "app.jar"]
