#!/bin/bash
# scripts/docker-backup-db.sh
# Create a compressed MySQL database backup with timestamp.
# Backups are stored in backups/ relative to the repo root.

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${SCRIPT_DIR}/../docker/docker-compose.yml"
BACKUP_DIR="${SCRIPT_DIR}/../backups"

# Load environment
ENV_FILE="${SCRIPT_DIR}/../docker/.env"
if [ -f "${ENV_FILE}" ]; then
    # shellcheck disable=SC1090
    source "${ENV_FILE}"
fi

DB_NAME="${MYSQL_DATABASE:-fusion}"
DB_USER="${MYSQL_USER:-fusion}"
DB_PASSWORD="${MYSQL_PASSWORD:-fusionpassword}"
TIMESTAMP=$(date '+%Y%m%d_%H%M%S')
BACKUP_FILE="${BACKUP_DIR}/fusion_backup_${TIMESTAMP}.sql.gz"

mkdir -p "${BACKUP_DIR}"

echo -e "${BLUE}Creating database backup...${NC}"

docker compose -f "${COMPOSE_FILE}" exec -T mysql \
    mysqldump -u "${DB_USER}" -p"${DB_PASSWORD}" \
    --single-transaction \
    --routines \
    --triggers \
    "${DB_NAME}" | gzip > "${BACKUP_FILE}"

BACKUP_SIZE=$(du -sh "${BACKUP_FILE}" | cut -f1)
echo -e "${GREEN}✓ Backup created: ${BACKUP_FILE} (${BACKUP_SIZE})${NC}"
