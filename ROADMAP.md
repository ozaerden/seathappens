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
- ELK local integration
- Structured JSON log shipping to Logstash
- Kibana searchable application logs

### Audit Logging

- Kafka-backed audit event publishing
- MongoDB audit log persistence
- Audit events for auth, payment, and ticket validation
- `occurredAt` timestamp with local timezone offset in audit documents

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

## Finalization Status

The core backend learning goals are now complete enough to treat the project as a finished modular-monolith portfolio project.

No microservice extraction is required to finalize the project. The current architecture should stay as a modular monolith with clear boundaries and production-oriented infrastructure patterns.

Recommended finalization work:

- keep documentation current
- keep local Docker infrastructure reproducible
- add tests only for flows that become risky to change
- avoid adding new infrastructure unless it teaches a clear concept

## Optional Future Work

### Product Flow Polish

- Decide whether customer-facing list-all endpoints should be renamed or hidden in favor of `/my`.
- Add richer ticket validation response details if needed.
- Consider a separate staff role for venue scanning instead of using `ADMIN`.
- Add admin audit search endpoints if Mongo shell queries become inconvenient.

### API Gateway

Optional separate gateway application.

Possible features:

- JWT validation
- token revocation check
- rate limiting
- circuit breaker
- routing to the monolith

This is useful for learning gateway patterns, but it is not required for the monolith to be considered complete.

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

### Kubernetes

Optional learning target.

- Deployments
- Services
- Ingress
- ConfigMaps
- Secrets

### CI/CD

Optional learning target.

- GitHub Actions
- Docker build
- Automated tests
- Deployment pipelines

### Cloud Deployment

Optional learning target.

Potential targets:

- AWS
- Azure
- Kubernetes cluster

## Known Technical Debt

No blocking technical debt remains for the current modular-monolith scope.

Optional follow-ups:

- Decide whether logout should revoke only the current session or all sessions.
- Consider a dedicated staff role for ticket scanning instead of using `ADMIN`.
- Add admin audit search endpoints if Compass or shell queries become inconvenient.
- Add API Gateway, Kubernetes, CI/CD, or cloud deployment only as separate learning exercises.
