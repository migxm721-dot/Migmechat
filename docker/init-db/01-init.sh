#!/bin/bash
# docker/init-db/01-init.sh
# Runs inside the MySQL container on first startup.
# Creates additional database users with appropriate privileges.

set -e

echo "[init-db] Creating additional database users..."

mysql -u root -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    -- Read-only user for reporting and analytics
    CREATE USER IF NOT EXISTS 'readonly'@'%' IDENTIFIED BY '${MYSQL_READONLY_PASSWORD:-readonly_change_me}';
    GRANT SELECT ON ${MYSQL_DATABASE}.* TO 'readonly'@'%';

    -- Backup user with minimal required privileges
    CREATE USER IF NOT EXISTS 'backup'@'localhost' IDENTIFIED BY '${MYSQL_BACKUP_PASSWORD:-backup_change_me}';
    GRANT SELECT, LOCK TABLES, SHOW VIEW, EVENT, TRIGGER ON ${MYSQL_DATABASE}.* TO 'backup'@'localhost';

    FLUSH PRIVILEGES;
EOSQL

echo "[init-db] Additional users created successfully."
