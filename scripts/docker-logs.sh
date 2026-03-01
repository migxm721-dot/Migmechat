#!/bin/bash
# scripts/docker-logs.sh
# View logs for all or a specific Migmechat Docker service.
# Usage:
#   bash scripts/docker-logs.sh             # all services
#   bash scripts/docker-logs.sh gateway     # single service

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${SCRIPT_DIR}/../docker/docker-compose.yml"

SERVICE="${1:-}"

if [ -n "${SERVICE}" ]; then
    docker compose -f "${COMPOSE_FILE}" logs -f "${SERVICE}"
else
    docker compose -f "${COMPOSE_FILE}" logs -f
fi
