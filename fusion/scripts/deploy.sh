#!/bin/bash
set -e

echo "Deploying Fusion Backend..."
cd "$(dirname "$0")/.."

# Build Docker image
./scripts/docker-build.sh

# Deploy with docker-compose
docker-compose -f docker/docker-compose.yml up -d

echo "Deployment complete!"
echo "REST API: http://localhost:8080"
echo "TCP Server: localhost:9119"
echo "Health: http://localhost:8080/actuator/health"
