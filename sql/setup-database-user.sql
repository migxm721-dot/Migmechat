-- Database User Setup Script for Migmechat
-- This script creates the 'fusion' user and grants necessary permissions
-- for both local and Docker network access
--
-- IMPORTANT: This script fixes the error:
-- "java.sql.SQLException: Access denied for user 'fusion'@'172.17.0.1' (using password: YES)"
--
-- The error occurs when the application runs in a Docker container (IP 172.17.0.1)
-- trying to connect to MySQL, but the user lacks permissions from that IP range.

-- Create 'fusion' database if it doesn't exist
CREATE DATABASE IF NOT EXISTS fusion CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create 'stats' database if it doesn't exist (required by MonitorDB)
CREATE DATABASE IF NOT EXISTS stats CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Drop existing users to ensure clean setup (be careful in production!)
-- Uncomment the following lines only for fresh installation
-- DROP USER IF EXISTS 'fusion'@'localhost';
-- DROP USER IF EXISTS 'fusion'@'127.0.0.1';
-- DROP USER IF EXISTS 'fusion'@'172.17.0.%';
-- DROP USER IF EXISTS 'fusion'@'%';

-- Create user 'fusion' with password 'root123' for localhost access
-- This user is used by etc/database.properties and etc/fusion_db_write.properties
CREATE USER IF NOT EXISTS 'fusion'@'localhost' IDENTIFIED BY 'root123';

-- Create user 'fusion' for 127.0.0.1 access
-- This user is used when connecting via TCP to localhost
CREATE USER IF NOT EXISTS 'fusion'@'127.0.0.1' IDENTIFIED BY 'root123';

-- Create user 'fusion' for Docker network access (172.17.0.0/16 subnet)
-- This fixes the "Access denied for user 'fusion'@'172.17.0.1'" error
-- Used by etc/fusion_db_read.properties (172.17.0.2) and Docker container access
CREATE USER IF NOT EXISTS 'fusion'@'172.17.0.%' IDENTIFIED BY 'root123';

-- Grant all privileges on 'fusion' database for localhost
GRANT ALL PRIVILEGES ON fusion.* TO 'fusion'@'localhost';
GRANT ALL PRIVILEGES ON stats.* TO 'fusion'@'localhost';

-- Grant all privileges on 'fusion' database for 127.0.0.1
GRANT ALL PRIVILEGES ON fusion.* TO 'fusion'@'127.0.0.1';
GRANT ALL PRIVILEGES ON stats.* TO 'fusion'@'127.0.0.1';

-- Grant all privileges on 'fusion' database for Docker network
GRANT ALL PRIVILEGES ON fusion.* TO 'fusion'@'172.17.0.%';
GRANT ALL PRIVILEGES ON stats.* TO 'fusion'@'172.17.0.%';

-- Apply the privilege changes
FLUSH PRIVILEGES;

-- Verify the users were created
SELECT User, Host FROM mysql.user WHERE User = 'fusion';

-- Display the grants for verification
SHOW GRANTS FOR 'fusion'@'localhost';
SHOW GRANTS FOR 'fusion'@'127.0.0.1';
SHOW GRANTS FOR 'fusion'@'172.17.0.%';
