#!/bin/bash
# scripts/docker-restore-db.sh
# Restore a MySQL database from a backup file.
# Usage: bash scripts/docker-restore-db.sh <backup-file>

set -e

RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${SCRIPT_DIR}/../docker/docker-compose.yml"

BACKUP_FILE="${1:-}"

if [ -z "${BACKUP_FILE}" ]; then
    echo "Usage: $0 <backup-file>"
    echo "Example: $0 backups/fusion_backup_20240101_120000.sql.gz"
    exit 1
fi

if [ ! -f "${BACKUP_FILE}" ]; then
    echo -e "${RED}Error: Backup file not found: ${BACKUP_FILE}${NC}"
    exit 1
fi

# Load environment
ENV_FILE="${SCRIPT_DIR}/../docker/.env"
if [ -f "${ENV_FILE}" ]; then
    # shellcheck disable=SC1090
    source "${ENV_FILE}"
fi

DB_NAME="${MYSQL_DATABASE:-fusion}"
DB_USER="${MYSQL_USER:-fusion}"
DB_PASSWORD="${MYSQL_PASSWORD:-fusionpassword}"

echo -e "${YELLOW}WARNING: This will overwrite all data in database '${DB_NAME}'!${NC}"
read -rp "Type 'yes' to confirm restore from '${BACKUP_FILE}': " CONFIRM

if [ "${CONFIRM}" != "yes" ]; then
    echo "Aborted."
    exit 0
fi

echo -e "${BLUE}Restoring database from ${BACKUP_FILE}...${NC}"

if [[ "${BACKUP_FILE}" == *.gz ]]; then
    gunzip -c "${BACKUP_FILE}" | docker compose -f "${COMPOSE_FILE}" exec -T mysql \
        mysql -u "${DB_USER}" -p"${DB_PASSWORD}" "${DB_NAME}"
else
    docker compose -f "${COMPOSE_FILE}" exec -T mysql \
        mysql -u "${DB_USER}" -p"${DB_PASSWORD}" "${DB_NAME}" < "${BACKUP_FILE}"
fi

echo -e "${GREEN}✓ Database restored successfully from ${BACKUP_FILE}.${NC}"
