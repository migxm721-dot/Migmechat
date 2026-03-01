#!/bin/bash
# scripts/docker-stop.sh
# Stop all Migmechat Docker services.

set -e

BLUE='\033[0;34m'
GREEN='\033[0;32m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${SCRIPT_DIR}/../docker/docker-compose.yml"

echo -e "${BLUE}Stopping Migmechat services...${NC}"
docker compose -f "${COMPOSE_FILE}" down "$@"
echo -e "${GREEN}✓ All services stopped.${NC}"
