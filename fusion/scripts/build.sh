#!/bin/bash
set -e

echo "Building Fusion Backend..."
cd "$(dirname "$0")/.."

mvn clean package -f pom-springboot.xml -DskipTests

echo "Build complete: target/fusion-backend-1.0.0.jar"
