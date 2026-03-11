# Database Setup Guide for Migmechat

## Issue: Access Denied Error

If you encounter the following error:
```
java.sql.SQLException: Access denied for user 'fusion'@'172.17.0.1' (using password: YES)
```

This means the MySQL user 'fusion' doesn't have permission to connect from the Docker container network (172.17.0.0/16).

## Solution

### 1. Run the Database User Setup Script

Execute the SQL script to create the user with proper permissions:

```bash
# Connect to MySQL as root
mysql -u root -p < sql/setup-database-user.sql
```

Or manually execute in MySQL:

```bash
mysql -u root -p
```

Then source the script:
```sql
source /path/to/sql/setup-database-user.sql;
```

### 2. What the Script Does

The script (`sql/setup-database-user.sql`) performs the following actions:

1. Creates the `fusion` and `stats` databases if they don't exist
2. Creates the `fusion` user with three different host patterns:
   - `'fusion'@'localhost'` - For local connections
   - `'fusion'@'127.0.0.1'` - For TCP connections to localhost
   - `'fusion'@'172.17.0.%'` - For Docker container connections (fixes the access denied error)
3. Grants all privileges on both databases to all three user/host combinations
4. Flushes privileges to apply changes immediately

### 3. Verify the Setup

After running the script, verify the user was created correctly:

```sql
SELECT User, Host FROM mysql.user WHERE User = 'fusion';
```

You should see three entries:
```
+--------+------------+
| User   | Host       |
+--------+------------+
| fusion | localhost  |
| fusion | 127.0.0.1  |
| fusion | 172.17.0.% |
+--------+------------+
```

### 4. Database Configuration Files

The application uses several database configuration files:

- **etc/database.properties** - Main database connection pool configuration
- **etc/fusion_db_read.properties** - Read-only database connection (uses 172.17.0.2)
- **etc/fusion_db_write.properties** - Write database connection (uses 127.0.0.1)
- **etc/olap_db_read.properties** - OLAP database connection

### 5. Docker Networking

When running in Docker:
- The application container typically gets IP addresses in the 172.17.0.0/16 range (e.g., 172.17.0.1)
- The MySQL container is often at 172.17.0.2
- MySQL's user permissions are based on the connecting host IP address
- The wildcard pattern `172.17.0.%` matches all IPs in the 172.17.0.0-255 range

### 6. Security Notes

**Important**: The default password is `root123`. For production environments:

1. Change the password in the SQL script before running it
2. Update all database.properties files with the new password
3. Use environment variables or secrets management for passwords
4. Restrict network access using MySQL's host patterns more precisely
5. Enable SSL/TLS for database connections

### 7. Alternative Solutions

If you cannot modify MySQL user permissions, you can:

1. **Use hostname-based connections**: Configure MySQL and Docker to use hostnames instead of IP addresses
2. **Create user with '%' wildcard**: `CREATE USER 'fusion'@'%'` (less secure, allows connections from any host)
3. **Configure Docker networking**: Use Docker's host network mode or custom bridge networks

### 8. Troubleshooting

#### Connection still fails after setup
- Verify MySQL is listening on the correct network interface
- Check MySQL's `bind-address` in my.cnf (should be 0.0.0.0 for Docker)
- Ensure firewall rules allow connections from Docker network
- Verify password is correct in all configuration files

#### Can't connect to MySQL from container
- Check if MySQL container is running: `docker ps`
- Verify network connectivity: `docker exec -it <container> ping 172.17.0.2`
- Check MySQL logs for connection attempts: `docker logs <mysql-container>`

#### Permission denied even with correct user
- Run `FLUSH PRIVILEGES;` in MySQL
- Restart MySQL server
- Verify the host pattern matches the actual IP address

### 9. Configuration File Locations

```
Migmechat/
├── etc/
│   ├── database.properties          # Main database config
│   ├── fusion_db_read.properties    # Read replica config
│   ├── fusion_db_write.properties   # Write master config
│   └── olap_db_read.properties      # OLAP database config
├── sql/
│   └── setup-database-user.sql      # User setup script
└── unixbin/
    ├── database.properties          # Unix binary config
    ├── fusion_db_read.properties    # Unix read config
    └── fusion_db_write.properties   # Unix write config
```

### 10. Quick Fix for Development

For development environments only, you can run:

```sql
-- DEVELOPMENT ONLY - NOT FOR PRODUCTION
CREATE USER 'fusion'@'%' IDENTIFIED BY 'root123';
GRANT ALL PRIVILEGES ON fusion.* TO 'fusion'@'%';
GRANT ALL PRIVILEGES ON stats.* TO 'fusion'@'%';
FLUSH PRIVILEGES;
```

This allows connections from any host but is **not recommended for production** due to security concerns.
