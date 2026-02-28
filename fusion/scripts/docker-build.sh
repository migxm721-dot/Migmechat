#!/bin/bash
set -e

echo "Building Fusion Backend Docker image..."
cd "$(dirname "$0")/.."

# Build JAR first
./scripts/build.sh

# Build Docker image
docker build -t fusion-backend:latest -f docker/Dockerfile .

echo "Docker image built: fusion-backend:latest"
