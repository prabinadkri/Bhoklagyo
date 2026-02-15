# Bhoklagyo

A production-grade food ordering and restaurant management platform built with **Spring Boot 3.5**, **PostgreSQL + PostGIS**, **Redis**, **Kafka**, and a full observability stack. Designed to demonstrate industry-standard backend engineering practices — from containerization to Kubernetes deployment.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Runtime** | Java 21, Spring Boot 3.5.7 |
| **Database** | PostgreSQL 16 + PostGIS (geospatial queries) |
| **Migrations** | Flyway |
| **Cache** | Redis 7 (Spring Cache + custom TTLs) |
| **Messaging** | Apache Kafka (Confluent 7.6) |
| **Auth** | JWT (HS256, jjwt 0.12.3), stateless sessions, BCrypt |
| **Real-time** | WebSocket / STOMP (SockJS) |
| **API Docs** | OpenAPI 3.0 / Swagger UI (springdoc) |
| **Observability** | Prometheus + Grafana + Loki + Promtail, Micrometer |
| **Containerization** | Docker, Docker Compose |
| **Orchestration** | Kubernetes (manifests + HPA + Ingress + NetworkPolicy) |
| **CI/CD** | GitHub Actions (build → test → Docker push → k6 smoke) |
| **Load Testing** | k6 (smoke, load, stress, spike) |

---

## Features

### Core Business
- **Restaurant Management** — CRUD operations, menu management, image uploads, opening hours, featured restaurants
- **Search** — Full-text search across restaurant names, menu items, cuisine tags, and dietary tags with cursor-based pagination
- **Search Sorting** — Sort by relevance (default), rating (highest first), or price (cheapest average-for-one first)
- **Nearby Search** — Geospatial queries using PostGIS `ST_DWithin` and `ST_Distance`
- **Order Processing** — Create orders, update status (PENDING → CONFIRMED → PREPARING → READY → DELIVERED), cancel orders
- **Customer Feedback** — Rate and review completed orders; aggregated restaurant ratings
- **Notifications** — In-app notifications for order status changes, employee invitations
- **Real-time Updates** — WebSocket/STOMP push for live order status changes
- **Vendor Management** — Admin-managed vendor registry
- **Document Management** — Upload and link documents to restaurants
- **Restaurant Requests** — Users can submit restaurant listing requests; admin approval workflow

### Authentication & Authorization
- **JWT Authentication** — Stateless token-based auth with 24h expiry
- **Role-Based Access** — `CUSTOMER`, `OWNER`, `EMPLOYEE`, `ADMIN` roles
- **Admin Registration** — Guarded by server-side secret
- **Employee Invitation** — Token-based email invitation flow; owners invite employees to their restaurants

### Infrastructure & Production Readiness
- **Redis Caching** — Per-cache TTLs (restaurants: 10m, search: 3m, menu: 10m); graceful degradation when Redis is down
- **Redis Rate Limiting** — Sliding-window IP-based rate limiter (100 req/60s); fail-open on Redis failure
- **Kafka Event Streaming** — Order and notification events with dead-letter topics and retry
- **Structured Audit Logging** — Security-critical operations logged in key=value format for Loki/Splunk ingestion
- **Input Sanitization** — HTML-escape user inputs to prevent XSS
- **Security Headers** — CSP, HSTS, X-Frame-Options, X-Content-Type-Options, Referrer-Policy, Permissions-Policy
- **Request Tracing** — Correlation IDs (X-Request-ID) propagated through MDC into logs and Kafka events
- **API Versioning Headers** — X-API-Version header on every response
- **Graceful Shutdown** — 30s drain period for in-flight requests (Kubernetes-ready)

---

## Project Structure

```
├── src/main/java/com/example/Bhoklagyo/
│   ├── config/          # Security, Redis, Kafka, WebSocket, OpenAPI, CORS, rate limiting
│   ├── controller/      # REST controllers (15 controllers)
│   ├── dto/             # Request/Response DTOs (35+)
│   ├── entity/          # JPA entities (17 entities)
│   ├── event/           # Kafka event publisher + consumers
│   ├── exception/       # Global exception handler + custom exceptions
│   ├── mapper/          # Entity → DTO mappers
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT utility, Invite token utility
│   ├── service/         # Business logic (interface + impl)
│   ├── util/            # Input sanitizer, backfill runners
│   └── websocket/       # WebSocket event sender + subscription interceptor
├── src/main/resources/
│   ├── application.properties
│   ├── logback-spring.xml
│   └── db/migration/    # Flyway SQL migrations
├── k8s/                 # Kubernetes manifests
├── load-tests/          # k6 performance test scripts
├── infrastructure/      # Prometheus, Grafana, Loki, Promtail configs
├── .github/workflows/   # CI/CD pipeline
├── docker-compose.yml   # Full local stack
├── Dockerfile           # Multi-stage production build
└── seed_database.sql    # Sample data (40 restaurants)
```

---

## Quick Start

### Prerequisites

- **Java 21** (or Docker)
- **PostgreSQL 16** with PostGIS extension
- **Redis 7** (optional — app degrades gracefully without it)
- **Apache Kafka** (optional — events are fail-open)

### Option 1: Docker Compose (recommended)

Start the entire stack with one command:

```bash
docker compose up -d
```

This starts: **App** (8080) · **PostgreSQL+PostGIS** (5432) · **Redis** (6379) · **Kafka** (9092) · **Kafka UI** (8090) · **Mailpit** (8025) · **Prometheus** (9090) · **Grafana** (3000) · **Loki** (3100)

```bash
# Verify health
curl http://localhost:8080/actuator/health

# Open Swagger UI
open http://localhost:8080/swagger-ui.html

# Open Grafana dashboards (admin/admin)
open http://localhost:3000

# Open Kafka UI
open http://localhost:8090

# Open Mailpit (email inbox)
open http://localhost:8025
```

### Option 2: Local Development

```bash
# 1. Start PostgreSQL with PostGIS and create database
createdb bhoklagyo
psql -d bhoklagyo -c "CREATE EXTENSION IF NOT EXISTS postgis;"

# 2. (Optional) Start Redis
redis-server

# 3. Set required environment variables
export JWT_SECRET="your-secret-key-at-least-32-chars-long"
export ADMIN_REGISTRATION_SECRET="your-admin-secret"

# 4. Run the application
./mvnw spring-boot:run

# 5. (Optional) Seed sample data
psql -U bhokuser -d bhoklagyo -f seed_database.sql
```

The app starts at `http://localhost:8080`.

### Option 3: Kubernetes

```bash
# Deploy the full stack
./k8s/deploy.sh

# Preview without applying
./k8s/deploy.sh --dry-run

# Tear down
./k8s/deploy.sh --delete

# Port-forward to access locally
kubectl port-forward svc/bhoklagyo-app 8080:80 -n bhoklagyo
```

---

## API Endpoints

### Public
| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login` | Login and get JWT token |
| `POST` | `/admin/register` | Register admin (requires secret) |
| `POST` | `/admin/login` | Admin login |
| `GET` | `/restaurants` | List restaurants (cursor pagination) |
| `GET` | `/restaurants/{id}` | Restaurant details |
| `GET` | `/restaurants/{id}/menu` | Restaurant menu |
| `GET` | `/restaurants/featured` | Featured restaurants |
| `GET` | `/search?keyword=pizza` | Search (name, menu, tags) |
| `GET` | `/search/rating?keyword=momo` | Search sorted by rating |
| `GET` | `/search/price?keyword=thali` | Search sorted by price |
| `GET` | `/search/nearby?keyword=...&latitude=...&longitude=...` | Nearby search |
| `GET` | `/v3/api-docs` | OpenAPI spec |
| `GET` | `/swagger-ui.html` | Swagger UI |
| `GET` | `/actuator/health` | Health check |
| `GET` | `/actuator/prometheus` | Prometheus metrics |

### Authenticated (Bearer JWT)
| Method | Path | Role | Description |
|--------|------|------|-------------|
| `POST` | `/restaurants` | ADMIN | Create restaurant |
| `POST` | `/restaurants/{id}/menu` | OWNER/EMPLOYEE | Add menu items |
| `PATCH` | `/restaurants/{id}/menu/{itemId}` | OWNER/EMPLOYEE | Update menu item |
| `DELETE` | `/restaurants/{id}/menu/{itemId}` | OWNER/EMPLOYEE | Delete menu item |
| `POST` | `/restaurants/{id}/upload-image` | ADMIN/OWNER | Upload restaurant image |
| `PATCH` | `/restaurants/{id}/is-open` | OWNER | Toggle open/closed |
| `POST` | `/restaurants/{rid}/orders` | AUTH | Place an order |
| `GET` | `/restaurants/{rid}/orders` | AUTH | List restaurant orders |
| `PATCH` | `/restaurants/{rid}/orders/{oid}` | AUTH | Update order status |
| `GET` | `/orders` | AUTH | My orders (customer) |
| `POST` | `/orders/{oid}/feedback` | AUTH | Submit order feedback |
| `GET` | `/notifications/user` | AUTH | My notifications |
| `POST` | `/restaurants/{id}/invite-employee` | OWNER | Invite employee by email |
| `GET` | `/admin/dashboard` | ADMIN | Admin dashboard stats |
| `PATCH` | `/admin/users/{id}/role` | ADMIN | Change user role |

### WebSocket
| Endpoint | Description |
|----------|-------------|
| `ws://host/ws` (SockJS) | STOMP connect |
| `/topic/restaurant/{id}/orders` | Live order events for restaurant |
| `/user/queue/notifications` | User-specific notifications |

---

## Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `JWT_SECRET` | **Yes** | — | JWT signing key (≥32 chars) |
| `ADMIN_REGISTRATION_SECRET` | **Yes** | — | Secret for admin registration |
| `SPRING_DATASOURCE_URL` | No | `jdbc:postgresql://localhost:5432/bhoklagyo` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | No | `bhokuser` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | No | `root` | DB password |
| `SPRING_DATA_REDIS_HOST` | No | `localhost` | Redis host |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | No | `localhost:9092` | Kafka brokers |
| `RATE_LIMIT_REQUESTS` | No | `100` | Rate limit per window |
| `RATE_LIMIT_WINDOW_SECONDS` | No | `60` | Rate limit window |

---

## Running Tests

```bash
# Unit + integration tests
./mvnw verify

# With coverage report (JaCoCo)
./mvnw verify
open target/site/jacoco/index.html
```

---

## Load Testing (k6)

Install [k6](https://k6.io/docs/get-started/installation/), then:

```bash
# Smoke test — sanity check (2 VUs, 30s)
k6 run load-tests/smoke.js

# Load test — sustained peak traffic (50 VUs, 10min)
k6 run load-tests/load.js

# Stress test — find breaking point (200 VUs, 13min)
k6 run load-tests/stress.js

# Spike test — flash crowd simulation (200 VUs spike, ~4.5min)
k6 run load-tests/spike.js
```

Override environment:
```bash
k6 run -e BASE_URL=http://your-server:8080 -e AUTH_EMAIL=user@test.com -e AUTH_PASSWORD=pass123 load-tests/load.js
```

---

## Observability

| Service | URL | Credentials |
|---------|-----|-------------|
| Grafana | http://localhost:3000 | admin / admin |
| Prometheus | http://localhost:9090 | — |
| Kafka UI | http://localhost:8090 | — |
| Mailpit | http://localhost:8025 | — |
| Swagger | http://localhost:8080/swagger-ui.html | — |

### Available Metrics
- `http.server.requests` — Request rate, latency (p50/p95/p99), error rate
- `jvm.memory.used` — Heap and non-heap memory
- `hikaricp.connections.*` — Database connection pool stats
- `bhoklagyo.orders.created` — Custom order event counters
- `bhoklagyo.notifications.sent` — Notification delivery count
- `process.cpu.usage` — CPU utilization

---

## License

MIT
