# migmereborn

A real-time chat application built with Spring Boot (backend) and React (frontend).

## Project Structure

```
.
├── backend/    # Spring Boot (Java/Maven) — REST API + WebSocket + static file serving
└── frontend/   # React SPA (Vite)
```

## Quick Start (Local Dev)

### Prerequisites
- Java 17+, Maven 3.8+
- Node.js 18+, npm 9+

### Run backend (API only, port 8000)
```bash
mvn -f backend/pom.xml spring-boot:run
```

### Run frontend dev server (port 5173, proxies /api and /ws to backend)
```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 in your browser.

## Build for Production (SPA served by Spring Boot)

```bash
# 1. Build React app — output goes directly into backend static resources
cd frontend
npm install
npm run build

# 2. Start the combined server on port 8000
mvn -f backend/pom.xml spring-boot:run
```

Open http://localhost:8000 — the Spring Boot server serves both the API and the React SPA.

## Running on Replit

1. In the Replit Shell, run:
   ```bash
   cd frontend && npm install && npm run build && cd ..
   mvn -f backend/pom.xml spring-boot:run
   ```
2. Replit will detect port 8000 and open the webview automatically.
3. For live frontend dev on Replit, run both the backend (`mvn spring-boot:run`) and frontend (`npm run dev`) in separate shells and access the Vite port.

## API Reference

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/rooms` | List all rooms |
| GET | `/api/rooms/{roomId}/messages?limit=50` | Get message history |
| POST | `/api/rooms/{roomId}/messages` | Post a message (also broadcasts via WebSocket) |

### WebSocket (STOMP over SockJS)
- Endpoint: `/ws`
- Send: `/app/rooms/{roomId}/send`
- Subscribe: `/topic/rooms/{roomId}`

