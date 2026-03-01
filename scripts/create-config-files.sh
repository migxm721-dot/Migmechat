#!/bin/bash
# scripts/create-config-files.sh
# Automated setup script that creates all required directories and copies
# .example configuration files to their actual config paths.
# Safe to run multiple times (idempotent).

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(dirname "${SCRIPT_DIR}")"

log()    { echo -e "${BLUE}[setup]${NC} $*"; }
success(){ echo -e "${GREEN}[setup] ✓${NC} $*"; }
warn()   { echo -e "${YELLOW}[setup] ⚠${NC} $*"; }

echo ""
echo "========================================"
echo "  Migmechat Configuration Setup"
echo "========================================"
echo ""

# ─── Create Directories ───────────────────────────────────────────────────────
log "Creating required directories..."
mkdir -p "${REPO_ROOT}/config"
mkdir -p "${REPO_ROOT}/docker"
mkdir -p "${REPO_ROOT}/logs"
mkdir -p "${REPO_ROOT}/backups"
mkdir -p "${REPO_ROOT}/etc"
success "Directories created."

# ─── Copy Example Files ───────────────────────────────────────────────────────
copy_example() {
    local src="$1"
    local dst="$2"
    if [ -f "${src}" ]; then
        if [ -f "${dst}" ]; then
            warn "Already exists, skipping: ${dst}"
        else
            cp "${src}" "${dst}"
            success "Created: ${dst}"
        fi
    else
        warn "Example file not found: ${src}"
    fi
}

log "Copying configuration templates..."

copy_example "${REPO_ROOT}/config/database.properties.example" \
             "${REPO_ROOT}/etc/database.properties"

copy_example "${REPO_ROOT}/config/gateway.properties.example" \
             "${REPO_ROOT}/etc/GatewayTCP.cfg"

copy_example "${REPO_ROOT}/docker/.env.example" \
             "${REPO_ROOT}/docker/.env"

# ─── Make Scripts Executable ──────────────────────────────────────────────────
log "Making scripts executable..."
chmod +x "${REPO_ROOT}/scripts"/*.sh
chmod +x "${REPO_ROOT}/docker/docker-entrypoint.sh"
chmod +x "${REPO_ROOT}/docker/init-db/01-init.sh"
success "Scripts are executable."

# ─── Create .gitignore Entries ────────────────────────────────────────────────
log "Checking .gitignore for sensitive files..."
GITIGNORE="${REPO_ROOT}/.gitignore"
ADDITIONS=(
    "etc/database.properties"
    "etc/*.properties"
    "docker/.env"
    "backups/"
    "logs/"
)

for entry in "${ADDITIONS[@]}"; do
    if ! grep -qF "${entry}" "${GITIGNORE}" 2>/dev/null; then
        echo "${entry}" >> "${GITIGNORE}"
        success "Added to .gitignore: ${entry}"
    fi
done

# ─── Summary ──────────────────────────────────────────────────────────────────
echo ""
echo "========================================"
echo "  Setup Complete!"
echo "========================================"
echo ""
echo "Next steps:"
echo "  1. Edit etc/database.properties with your database credentials"
echo "  2. Edit docker/.env with your Docker environment settings"
echo "  3. Run: bash scripts/import-database.sh"
echo "  4. Run: mvn clean package -DskipTests"
echo "  5. Run: bash unixbin/registry.sh && bash unixbin/gatewaytcp.sh"
echo ""
echo "  Or for Docker deployment:"
echo "  5. Run: bash scripts/docker-start.sh"
echo ""
