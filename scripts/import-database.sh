#!/bin/bash
# scripts/import-database.sh
# Automated database import script for Migmechat.
# Imports SQL schemas in the correct dependency order.

set -e

# ─── Color Output ─────────────────────────────────────────────────────────────
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log()    { echo -e "${BLUE}[import-db]${NC} $*"; }
success(){ echo -e "${GREEN}[import-db] ✓${NC} $*"; }
warn()   { echo -e "${YELLOW}[import-db] ⚠${NC} $*"; }
error()  { echo -e "${RED}[import-db] ✗${NC} $*" >&2; }

# ─── Get Script Directory ─────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "${SCRIPT_DIR}")"
SQL_DIR="${REPO_ROOT}/sql"

# ─── Prompt for Credentials ───────────────────────────────────────────────────
echo ""
echo "========================================"
echo "  Migmechat Database Import"
echo "========================================"
echo ""

read -rp "MySQL host [localhost]: " DB_HOST
DB_HOST="${DB_HOST:-localhost}"

read -rp "MySQL port [3306]: " DB_PORT
DB_PORT="${DB_PORT:-3306}"

read -rp "MySQL database name [fusion]: " DB_NAME
DB_NAME="${DB_NAME:-fusion}"

read -rp "MySQL username [root]: " DB_USER
DB_USER="${DB_USER:-root}"

read -rsp "MySQL password: " DB_PASSWORD
echo ""

# ─── Test Connection ──────────────────────────────────────────────────────────
log "Testing database connection..."
export MYSQL_PWD="${DB_PASSWORD}"
if ! mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" \
     -e "SELECT 1;" > /dev/null 2>&1; then
    error "Cannot connect to MySQL at ${DB_HOST}:${DB_PORT} as user '${DB_USER}'."
    error "Please check your credentials and ensure MySQL is running."
    exit 1
fi
success "Connected to MySQL."

# ─── Create Database if Not Exists ────────────────────────────────────────────
log "Checking if database '${DB_NAME}' exists..."
DB_EXISTS=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" \
    -u "${DB_USER}" \
    -e "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME='${DB_NAME}';" \
    --skip-column-names 2>/dev/null || echo "")

if [ -z "${DB_EXISTS}" ]; then
    log "Database '${DB_NAME}' does not exist. Creating..."
    mysql -h "${DB_HOST}" -P "${DB_PORT}" -u "${DB_USER}" \
        -e "CREATE DATABASE \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    success "Database '${DB_NAME}' created."
else
    success "Database '${DB_NAME}' already exists."
fi

# ─── Import SQL Files in Order ────────────────────────────────────────────────
# Core schemas must be imported in this order (dependency chain)
ORDERED_FILES=(
    "userid.sql"
    "ANC.sql"
    "VAS.sql"
    "store.sql"
    "virtualgifts.sql"
    "virtualgifts2.sql"
    "badges.sql"
    "Bots.sql"
    "Location.sql"
    "ReferralSource.sql"
    "UserSetting.sql"
    "UserWall.sql"
    "broadcastList.sql"
    "pendingContact.sql"
    "ContactListVersion.sql"
    "chatroomextradata.sql"
    "commenting.sql"
    "group_3.0.sql"
    "GroupCategories.sql"
    "GroupThreads.sql"
    "registrationcontext.sql"
    "sessionarchive.sql"
    "ratings_voting.sql"
    "migbo.sql"
    "migbo_guardset.sql"
    "migWars.sql"
    "Version 4.1.sql"
)

echo ""
log "Starting import of ${#ORDERED_FILES[@]} core SQL files..."
echo ""

IMPORTED=0
SKIPPED=0
FAILED=0

import_file() {
    local sql_file="$1"
    local full_path="${SQL_DIR}/${sql_file}"

    if [ ! -f "${full_path}" ]; then
        warn "File not found, skipping: ${sql_file}"
        SKIPPED=$((SKIPPED + 1))
        return
    fi

    log "Importing: ${sql_file}"
    if mysql -h "${DB_HOST}" -P "${DB_PORT}" \
             -u "${DB_USER}" \
             "${DB_NAME}" < "${full_path}" 2>/dev/null; then
        success "Imported: ${sql_file}"
        IMPORTED=$((IMPORTED + 1))
    else
        warn "Import had errors (may be safe to ignore): ${sql_file}"
        FAILED=$((FAILED + 1))
    fi
}

for sql_file in "${ORDERED_FILES[@]}"; do
    import_file "${sql_file}"
done

# ─── Summary ──────────────────────────────────────────────────────────────────
echo ""
echo "========================================"
echo "  Import Summary"
echo "========================================"
success "Imported: ${IMPORTED} files"
[ "${SKIPPED}" -gt 0 ] && warn "Skipped:  ${SKIPPED} files (not found)"
[ "${FAILED}" -gt 0 ]  && warn "Errors:   ${FAILED} files (may need manual review)"
echo ""

# ─── Show Table Count ─────────────────────────────────────────────────────────
TABLE_COUNT=$(mysql -h "${DB_HOST}" -P "${DB_PORT}" \
    -u "${DB_USER}" \
    -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${DB_NAME}';" \
    --skip-column-names 2>/dev/null || echo "unknown")

success "Database '${DB_NAME}' now has ${TABLE_COUNT} tables."
echo ""
