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
- SMTP email delivery
- Mailtrap-compatible configuration
- HTML email content
- Ticket QR PNG attachments
- Notification status transitions
- Notification email retry support
- Admin notification list/detail endpoints
- Admin failed-notification retry endpoint

### Observability

- Correlation id per HTTP request
- Request logging
- `X-Correlation-Id` response header
- Correlation id propagation into outbox events
- Correlation id propagation into Kafka event envelopes

### Audit Logging

- Kafka-backed audit event publishing
- MongoDB audit log persistence
- Audit events for auth, payment, and ticket validation
- Istanbul timezone timestamp in audit documents

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

- Reservation ownership protection
- Order ownership protection
- Payment ownership protection
- Ticket ownership protection
- Admin backoffice visibility for orders and tickets

## Current Priority

### 1. ELK Stack

Next focus:

- Elasticsearch
- Logstash
- Kibana
- structured application logs
- centralized log search

Goal:

- make request logs, Kafka/outbox logs, notification logs, and audit-related logs searchable in one place
- keep MongoDB audit logs as business audit records, not as raw request/response logs

## Mid-Term

### Product Flow Polish

- Decide whether customer-facing list-all endpoints should be renamed or hidden in favor of `/my`.
- Add richer ticket validation response details if needed.
- Consider a separate staff role for venue scanning instead of using `ADMIN`.
- Add admin audit search endpoints if Mongo shell queries become inconvenient.

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

Reactive programming is optional and should stay mostly in the gateway. Do not rewrite the monolith to WebFlux just to practice `Mono` / `Flux`.

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

- ELK integration not implemented.
- API Gateway not implemented.
- No distributed deployment strategy yet.
- Dedicated staff role for ticket scanning is not implemented.
- Admin audit search endpoints are not implemented.
