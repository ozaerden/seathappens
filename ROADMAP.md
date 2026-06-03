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
- QR ticket generation
- Ticket validation / scanning simulation

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
- Refresh token flow
- Refresh token rotation

### Authorization

- Order ownership protection
- Ticket ownership protection
- Admin backoffice visibility for orders and tickets

## Current Priority

### 1. Notification Evolution

Current state:

- Notification records are persisted.
- Kafka consumer creates notification records for `PAYMENT_SUCCEEDED`.

Next:

- Simulated email sender.
- Notification status transitions.
- Notification retry support.
- Clear failure state for delivery simulation.

## Near Future

### Product Flow Polish

- Decide whether customer-facing list-all endpoints should be renamed or hidden in favor of `/my`.
- Add richer ticket validation response details if needed.
- Consider a separate staff role for venue scanning instead of using `ADMIN`.

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

- Correlation id not implemented.
- Centralized audit logging not implemented.
- ELK integration not implemented.
- API Gateway not implemented.
- Request/response tracing not implemented.
- Notification delivery is simulated by database persistence only.
- No distributed deployment strategy yet.
