# ROADMAP.md

## Completed

### Core Domain

- Venue management
- Event management
- Ticket type management
- Inventory management
- Reservation flow
- Order flow
- Payment flow
- Ticket generation

### Reliability

- Optimistic locking through `BaseEntity.@Version`
- Outbox pattern
- Kafka integration
- Outbox retry mechanism
- Retry backoff
- Idempotent consumers
- Processed event tracking with `processed_kafka_events`

### Notifications

- Notification persistence
- Kafka-driven notification creation

### Security

- User registration
- User login
- BCrypt password hashing
- JWT authentication
- Role-based authorization
- Redis-backed token revocation
- Logout endpoint
- User deactivation
- Immediate token invalidation

## Current Priority

### 1. Refresh Token Flow

Status: not implemented.

Goals:

- Generate refresh token at login.
- Store refresh token in Redis.
- Add refresh endpoint.
- Renew access tokens without requiring password login.
- Define refresh token expiration policy.
- Support refresh token revocation on logout and user deactivation.
- Keep access tokens short-lived.

Implementation notes to decide before coding:

- Whether refresh tokens should be opaque random tokens or JWTs.
- Whether refresh token rotation should be implemented immediately.
- Whether logout should revoke only the current session or all user sessions.

### 2. Ownership Expansion

Extend authenticated-user ownership checks to:

- Orders
- Tickets

Current state:

- Reservations are linked to `User`.
- Order creation already checks that the reservation belongs to the current user.
- `getMyOrders()` and `getMyTickets()` exist, but direct access by arbitrary id should be reviewed.

### 3. Notification Evolution

Current state:

- Notification records are persisted.
- Kafka consumer creates notification records for `PAYMENT_SUCCEEDED`.

Next:

- Simulated email sender.
- Notification status transitions.
- Notification retry support.
- Clear failure state for delivery simulation.

## Near Future

### Audit & Observability

- MongoDB audit log
- Request logging
- Response logging
- Correlation id
- Trace id

### ELK Stack

- Elasticsearch
- Logstash
- Kibana

Goals:

- centralized logs
- searchable audit history
- easier debugging of asynchronous flows

### QR Ticketing

- QR generation
- Ticket validation endpoint
- Ticket scanning simulation
- Ticket status transition review

## Mid-Term

### API Gateway

Separate gateway application.

Features:

- JWT validation
- token revocation check
- rate limiting
- circuit breaker
- routing

### Reactive Stack

Learn and apply:

- WebFlux
- Mono
- Flux

Primary target:

- API Gateway

### Distributed Architecture

Potential service extraction:

- Identity Service
- Ticketing Service
- Payment Service
- Notification Service

Only do this after the modular monolith is mature enough that boundaries are obvious.

## Long-Term

### Kubernetes

- Deployments
- Services
- Ingress
- ConfigMaps
- Secrets

### CI/CD

- GitHub Actions
- Docker build
- Automated tests
- Deployment pipelines

### Cloud Deployment

Potential targets:

- AWS
- Azure
- Kubernetes cluster

## Known Technical Debt

- Refresh token flow missing.
- Correlation id not implemented.
- Centralized audit logging not implemented.
- ELK integration not implemented.
- API Gateway not implemented.
- Request/response tracing not implemented.
- Notification delivery is simulated by database persistence only.
- No distributed deployment strategy yet.
- Direct entity id access should be reviewed for ownership enforcement.

