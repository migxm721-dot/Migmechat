#!/bin/bash
# scripts/docker-start.sh
# Quick start script to launch all Migmechat Docker services.

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${SCRIPT_DIR}/../docker/docker-compose.yml"

echo -e "${BLUE}Starting Migmechat services...${NC}"

# Copy .env if it doesn't exist
ENV_FILE="${SCRIPT_DIR}/../docker/.env"
ENV_EXAMPLE="${SCRIPT_DIR}/../docker/.env.example"
if [ ! -f "${ENV_FILE}" ] && [ -f "${ENV_EXAMPLE}" ]; then
    echo "No docker/.env found. Copying from .env.example..."
    cp "${ENV_EXAMPLE}" "${ENV_FILE}"
    echo "Please review docker/.env and update settings, then re-run this script."
    exit 1
fi

docker compose -f "${COMPOSE_FILE}" up -d "$@"

echo -e "${GREEN}✓ Services started. Use 'scripts/docker-logs.sh' to view logs.${NC}"
