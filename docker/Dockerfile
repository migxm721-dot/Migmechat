FROM openjdk:8-jdk-alpine

LABEL maintainer="Migmechat Team"
LABEL description="Migmechat Gateway Application"

# Install runtime dependencies
RUN apk add --no-cache \
    bash \
    curl \
    netcat-openbsd \
    mysql-client \
    tzdata \
    && rm -rf /var/cache/apk/*

# Create non-root user for security
RUN addgroup -S fusion && adduser -S fusion -G fusion

WORKDIR /app

# Copy build descriptor first (layer caching)
COPY pom.xml .

# Copy source and resources
COPY decompiled_src/ decompiled_src/
COPY sql/ sql/
COPY etc/ etc/
COPY config/ config/

# Copy startup script
COPY docker/docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Install Maven and build
RUN apk add --no-cache maven \
    && mvn clean package -DskipTests \
    && apk del maven \
    && rm -rf /root/.m2/repository

# Create required directories
RUN mkdir -p /app/logs /app/backups \
    && chown -R fusion:fusion /app

# Switch to non-root user
USER fusion

# Expose gateway and registry ports
EXPOSE 9119 10000

# Health check on gateway port
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD nc -z localhost 9119 || exit 1

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["gateway"]
