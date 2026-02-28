#!/bin/bash
set -e

echo "Running Fusion Backend locally..."
cd "$(dirname "$0")/.."

export SPRING_PROFILES_ACTIVE=dev
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=migme_dev
export DB_USERNAME=migme
export DB_PASSWORD=migme_dev_password
export REDIS_HOST=localhost
export REDIS_PORT=6379
export JWT_SECRET_KEY=dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci1mdXNpb24tYmFja2VuZA==

mvn spring-boot:run -f pom-springboot.xml
