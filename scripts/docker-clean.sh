#!/bin/bash
# scripts/docker-clean.sh
# Remove all Migmechat containers, volumes, images, and local data.
# WARNING: This is destructive and will delete all database data!

set -e

RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${SCRIPT_DIR}/../docker/docker-compose.yml"
DOCKER_DIR="${SCRIPT_DIR}/../docker"

echo -e "${RED}WARNING: This will permanently delete all containers, volumes, and data!${NC}"
echo -e "${YELLOW}This includes the MySQL database and all application data.${NC}"
echo ""
read -rp "Type 'yes' to confirm: " CONFIRM

if [ "${CONFIRM}" != "yes" ]; then
    echo "Aborted."
    exit 0
fi

echo ""
echo "Stopping and removing containers..."
docker compose -f "${COMPOSE_FILE}" down -v --remove-orphans 2>/dev/null || true

echo "Removing Migmechat Docker images..."
docker rmi migmechat-gateway:latest migmechat-registry:latest 2>/dev/null || true

echo "Removing local data directories..."
rm -rf "${DOCKER_DIR}/data" "${DOCKER_DIR}/logs"

echo ""
echo -e "${GREEN}✓ Cleanup complete. Run 'scripts/docker-start.sh' to start fresh.${NC}"
