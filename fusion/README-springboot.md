# Fusion Backend - Spring Boot 3.x Modernization

## Overview
This is the modernized Fusion backend built with Spring Boot 3.2.0 + Netty 4.1.x, replacing the legacy JBoss + Ice Framework.

## Architecture
- **Spring Boot 3.2.0** - Application framework
- **Netty 4.1.x** - TCP server for Fusion Binary Protocol (port 9119)
- **Spring Security 6.x + JWT** - Authentication & authorization
- **Spring Data JPA + MySQL 8.0** - Database layer
- **HikariCP** - Connection pooling
- **Redis 7.x + Caffeine** - Caching
- **Flyway** - Database migrations

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0
- Redis 7.x

### Build
```bash
./scripts/build.sh
```

### Run with Docker Compose
```bash
./scripts/deploy.sh
```

### Run Locally
```bash
./scripts/run-local.sh
```

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login and get JWT token

### Users
- `GET /api/v1/users/me` - Get current user profile
- `GET /api/v1/users/search?q={username}` - Search users

### Chat
- `GET /api/v1/chat/conversations/{id}/messages` - Get messages

### Health
- `GET /actuator/health` - Health check

## Fusion Protocol (Port 9119)

Android clients connect to port 9119 using the Fusion Binary Protocol:

### Packet Format
```
[1 byte: packet type][4 bytes: sequence id][4 bytes: payload length][N bytes: payload]
```

### Supported Packet Types
- LOGIN (0x01) / LOGIN_OK (0x02) / LOGIN_FAIL (0x03)
- MESSAGE (0x10) / MESSAGE_ACK (0x11)
- PRESENCE (0x20) / STATUS_MESSAGE (0x21)
- GET_CONTACTS (0x30) / CONTACT_LIST (0x31) / ADD_CONTACT (0x32) / REMOVE_CONTACT (0x33)
- HEARTBEAT (0x50) / HEARTBEAT_ACK (0x51)

## Configuration

Key environment variables:
```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=migme
DB_USERNAME=migme
DB_PASSWORD=your-password
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password
JWT_SECRET_KEY=your-base64-secret-key
SPRING_PROFILES_ACTIVE=prod
```
