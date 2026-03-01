# Migmechat Backend

A high-performance, real-time messaging backend built with Java and ZeroC Ice middleware. Migmechat provides a scalable chat platform with social features, virtual economy, and gamification.

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Layer                             │
│          Mobile Apps · Web Clients · Bots                       │
└─────────────────────────┬───────────────────────────────────────┘
                          │ TCP / HTTP / WebSocket
┌─────────────────────────▼───────────────────────────────────────┐
│                      Gateway Layer                              │
│   GatewayTCP · GatewayHTTP · GatewayWS  (ZeroC Ice)            │
└──────┬──────────────────────────────────────────────────────────┘
       │                         │
┌──────▼──────┐        ┌─────────▼──────────────────────────────┐
│   Registry  │        │            Service Layer                │
│  (IceGrid)  │        │  SMS Engine · Voice · Email · Bots     │
└─────────────┘        └─────────────────┬──────────────────────┘
                                         │
               ┌─────────────────────────▼────────────────────┐
               │                Data Layer                     │
               │   MySQL (primary) · Memcached (cache)         │
               └───────────────────────────────────────────────┘
```

## Features

| Category | Features |
|---|---|
| **Messaging** | Real-time chat, group rooms, private messages, message history |
| **Social** | Contact lists, user profiles, walls, notifications, ratings |
| **Virtual Economy** | Virtual gifts, store items, vouchers, merchant discounts |
| **Gamification** | Badges, scores, leaderboards, game bots (mig Warriors, Paint Wars) |
| **Media** | Emoticons, avatars, content management |
| **Infrastructure** | SMS engine, voice engine, email alerts, bot framework |

## Tech Stack

| Component | Technology | Version |
|---|---|---|
| Language | Java | 8+ |
| Middleware | ZeroC Ice | 3.7+ |
| Database | MySQL | 5.7+ |
| Cache | Memcached | 1.6+ |
| Build | Maven | 3.x |
| Connection Pool | C3P0 | latest |
| Logging | Log4j | 1.x |

---

## Prerequisites

- Java JDK 8 or higher
- Maven 3.x
- MySQL 5.7+
- Memcached 1.6+
- ZeroC Ice 3.7+
- Docker & Docker Compose (optional, for containerized deployment)

---

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/migxm721-dot/Migmechat.git
cd Migmechat
```

### 2. Set up the database

```bash
mysql -u root -p -e "CREATE DATABASE fusion CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p -e "CREATE USER 'fusion'@'localhost' IDENTIFIED BY 'your_password';"
mysql -u root -p -e "GRANT ALL PRIVILEGES ON fusion.* TO 'fusion'@'localhost';"
bash scripts/import-database.sh
```

### 3. Configure the application

```bash
bash scripts/create-config-files.sh
# Edit config files with your settings:
nano etc/database.properties
```

### 4. Build

```bash
mvn clean package -DskipTests
```

### 5. Run

```bash
# Start Ice Registry first
bash unixbin/registry.sh

# Start Gateway (TCP mode)
bash unixbin/gatewaytcp.sh

# Or start Gateway (HTTP mode)
bash unixbin/gatewayhttp.sh
```

---

## Configuration

### Database Configuration (`etc/database.properties`)

The database configuration uses C3P0 connection pooling. Copy the example and edit:

```bash
cp config/database.properties.example etc/database.properties
```

Key settings:

| Property | Description | Default |
|---|---|---|
| `database.jdbcUrl` | JDBC connection URL | `jdbc:mysql://localhost:3306/fusion` |
| `database.username` | Database user | `fusion` |
| `database.password` | Database password | *(required)* |
| `database.minPoolSize` | Minimum connections | `10` |
| `database.maxPoolSize` | Maximum connections | `100` |
| `global.maxIdleTime` | Max idle time (seconds) | `300` |

### Gateway Configuration

The gateway is configured via Ice configuration files (e.g. `etc/GatewayTCP.cfg`). See `config/gateway.properties.example` for all available settings.

| Setting | Description |
|---|---|
| `GatewayType` | Server type: `TCP`, `HTTP`, or `WS` |
| `GatewayHost` | Bind address |
| `GatewayPort` | Listen port (default: `9119`) |
| `Ice.Default.Locator` | Ice registry locator endpoint |

### Memcache Configuration

Memcached settings are managed in `etc/database.properties` (legacy) or a dedicated memcache properties file. See `config/memcache.properties.example`.

| Setting | Description | Default |
|---|---|---|
| `memcache.serverList` | Server list | `localhost:11211` |
| `memcache.timeout` | Connection timeout (ms) | `1000` |
| `session.expiry` | Session cache TTL (s) | `3600` |

---

## Database Setup

SQL scripts must be imported in this order to satisfy foreign key dependencies:

```bash
# Core tables (always required)
mysql -u fusion -p fusion < sql/userid.sql
mysql -u fusion -p fusion < sql/country.sql     # if present
mysql -u fusion -p fusion < sql/currency.sql    # if present
mysql -u fusion -p fusion < sql/ANC.sql
mysql -u fusion -p fusion < sql/VAS.sql
mysql -u fusion -p fusion < sql/store.sql
mysql -u fusion -p fusion < sql/virtualgifts.sql
mysql -u fusion -p fusion < sql/badges.sql
mysql -u fusion -p fusion < sql/Bots.sql

# Or use the automated script:
bash scripts/import-database.sh
```

---

## Building

```bash
# Full build (skip tests for faster build)
mvn clean package -DskipTests

# Full build with tests
mvn clean package

# Build and install to local Maven repo
mvn clean install -DskipTests
```

The compiled JAR is placed in `target/`.

---

## Running

### Without Docker

**Start Ice Registry:**
```bash
cd unixbin
./registry.sh
```

**Start Gateway (TCP):**
```bash
cd unixbin
sudo ./gatewaytcp.sh
```

**Start Gateway (HTTP):**
```bash
cd unixbin
./gatewayhttp.sh
```

**Start other services:**
```bash
./unixbin/smsengine.sh
./unixbin/emailalert.sh
./unixbin/voiceengine.sh
./unixbin/botserver.sh
./unixbin/monitor.sh
```

### JVM Options

The following JVM flags are recommended for production:

```bash
java -server \
     -Xmx1536m \
     -Dlog.dir=logs/ \
     -Dconfig.dir=etc/ \
     com.projectgoth.fusion.gateway.Gateway etc/GatewayTCP.cfg
```

---

## Docker Deployment

### Prerequisites

- Docker 20.10+
- Docker Compose 2.x

### Quick Start with Docker

```bash
cd docker

# Copy and edit environment file
cp .env.example .env
nano .env

# Start all services
bash ../scripts/docker-start.sh

# Or use docker compose directly
docker compose up -d
```

### Services

| Service | Port | Description |
|---|---|---|
| `mysql` | 3306 | MySQL 5.7 database |
| `memcache` | 11211 | Memcached cache |
| `registry` | 10000 | ZeroC Ice Registry |
| `gateway` | 9119, 10000 | Main application gateway |
| `phpmyadmin` | 8080 | Database management UI (admin profile) |
| `nginx` | 80, 443 | Reverse proxy (production profile) |

### Starting specific profiles

```bash
# Start core services + admin tools
docker compose --profile admin up -d

# Start core services + nginx (production)
docker compose --profile production up -d
```

### Docker Management Scripts

```bash
# Start all services
bash scripts/docker-start.sh

# Stop all services
bash scripts/docker-stop.sh

# Restart all services
bash scripts/docker-restart.sh

# View logs (all services)
bash scripts/docker-logs.sh

# View logs for specific service
bash scripts/docker-logs.sh gateway

# Backup database
bash scripts/docker-backup-db.sh

# Restore database from backup
bash scripts/docker-restore-db.sh backups/fusion_backup_20240101_120000.sql.gz

# Clean all containers, volumes, and data (destructive!)
bash scripts/docker-clean.sh
```

### Environment Variables

Copy `docker/.env.example` to `docker/.env` and configure:

```env
# Database
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=fusion
MYSQL_USER=fusion
MYSQL_PASSWORD=fusionpassword

# Gateway
GATEWAY_TYPE=TCP
GATEWAY_PORT=9119
GATEWAY_HOST=0.0.0.0

# Memcache
MEMCACHE_PORT=11211
```

---

## Project Structure

```
Migmechat/
├── config/                     # Configuration templates
│   ├── database.properties.example
│   ├── gateway.properties.example
│   └── memcache.properties.example
├── decompiled_src/             # Decompiled Java source
│   └── com/projectgoth/fusion/
│       ├── app/                # Application services
│       ├── gateway/            # Gateway implementations
│       ├── registry/           # Ice registry
│       ├── smsengine/          # SMS engine
│       ├── slice/              # Ice Slice interfaces
│       └── common/             # Shared utilities
├── docker/                     # Docker configuration
│   ├── Dockerfile              # Main application image
│   ├── Dockerfile.registry     # Ice Registry image
│   ├── docker-compose.yml      # Full stack orchestration
│   ├── docker-entrypoint.sh    # Container startup script
│   ├── .env.example            # Environment template
│   ├── init-db/                # Database initialization
│   └── mysql-conf/             # MySQL configuration
├── etc/                        # Runtime configuration
│   ├── database.properties     # Database settings (not in git)
│   └── queries.properties      # SQL queries
├── scripts/                    # Utility scripts
│   ├── import-database.sh      # Database import
│   ├── docker-*.sh             # Docker management
│   └── ...                     # Other admin scripts
├── sql/                        # Database schemas & migrations
├── src/                        # Java source (partial)
├── unixbin/                    # Unix startup scripts
└── pom.xml                     # Maven build descriptor
```

---

## Troubleshooting

### Application won't start

1. **Check Java version:** `java -version` (must be 8+)
2. **Check Ice is available:** verify `Ice.jar` is on classpath
3. **Check database connection:** `mysql -u fusion -p fusion -e "SELECT 1;"`
4. **Check Memcached:** `echo stats | nc localhost 11211`
5. **Check registry:** ensure `registry.sh` is running before gateway
6. **Review logs:** check `logs/` directory for error details

### Database connection issues

```bash
# Test connection
mysql -h localhost -P 3306 -u fusion -p fusion

# Check C3P0 pool config in etc/database.properties
# Ensure acquireRetryAttempts and acquireRetryDelay are set
```

### Docker issues

```bash
# View service logs
docker compose -f docker/docker-compose.yml logs -f gateway

# Check service health
docker compose -f docker/docker-compose.yml ps

# Recreate containers after config change
docker compose -f docker/docker-compose.yml up -d --force-recreate gateway

# Reset everything (WARNING: deletes all data)
bash scripts/docker-clean.sh
```

### Memcached connection issues

```bash
# Test Memcached connectivity
echo "stats" | nc localhost 11211

# Check server list in config
grep memcache etc/database.properties
```

### Port conflicts

Default ports used:

| Port | Service |
|---|---|
| 3306 | MySQL |
| 11211 | Memcached |
| 9119 | Gateway (TCP/HTTP) |
| 10000 | Ice Registry |

Change conflicting ports in `docker/.env` or the relevant config files.

---

## Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -am 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

Please ensure:
- Code follows existing style conventions
- SQL migrations are added to `sql/` with descriptive names
- Configuration properties are documented in example files
- Changes are tested locally before submitting

---

## License

This project is proprietary software. All rights reserved.

---

## Acknowledgments

- [ZeroC Ice](https://zeroc.com/) — High-performance middleware framework
- [C3P0](https://www.mchange.com/projects/c3p0/) — Connection pooling library
- [Log4j](https://logging.apache.org/log4j/) — Logging framework
- [MySQL](https://www.mysql.com/) — Relational database
- [Memcached](https://memcached.org/) — Distributed memory caching
