# AGENTS.md

## Project Overview

Seat Happens is a monolithic event ticketing backend application built with Java 26, Spring Boot 4, Maven, PostgreSQL, Flyway, Redis, and Kafka.

This is an IntelliJ-developed hobby project, but the design goal is serious: practice production-oriented backend engineering patterns and prepare for senior backend engineering interviews.

The project intentionally prioritizes:

- Clean, layered backend design
- Domain-driven modular structure
- Event-driven communication
- Reliability patterns
- Security best practices
- Production-oriented design decisions

The current codebase is a modular monolith. Keep it a modular monolith until the user explicitly asks to migrate parts into microservices.

## Architectural Principles

### Modular Monolith First

Modules are separated by business capability under `com.seathappens`.

Current modules:

- `venue`
- `event`
- `tickettype`
- `inventory`
- `reservation`
- `order`
- `payment`
- `ticket`
- `notification`
- `outbox`
- `auth`
- `user`
- `security`
- `common`

Do not introduce artificial microservice complexity inside the monolith. Future service extraction should be possible with minimal refactoring, but local development should stay simple and coherent.

### Layering

Prefer the existing package style:

- `controller` for REST API endpoints
- `service` for business logic and transaction boundaries
- `repository` for Spring Data JPA persistence
- `entity` for JPA entities and enums
- `dto.request` and `dto.response` for API payloads
- `config`, `scheduler`, `publisher`, and `consumer` where the module needs them

Controllers should delegate to services. Do not put business rules in controllers.

### Business Rules

#### Reservation

Lifecycle:

```text
ACTIVE -> CONVERTED -> EXPIRED -> CANCELLED
```

Rules:

- Only `ACTIVE` reservations can be converted to orders.
- Expired reservations release inventory.
- Cancelled reservations release inventory.
- Reservations are linked to the authenticated user.
- Reservation creation moves quantity from `availableQuantity` to `reservedQuantity`.

#### Order

Lifecycle:

```text
PENDING_PAYMENT -> PAID -> EXPIRED
```

Rules:

- Only `PENDING_PAYMENT` orders can be paid.
- Expired orders cannot be paid.
- Creating an order converts the reservation.
- Expiring a pending order releases the reserved inventory and marks the reservation expired.

#### Payment

Lifecycle:

```text
SUCCESS / FAILED
```

Rules:

- Successful payment finalizes inventory by moving quantity from `reservedQuantity` to `soldQuantity`.
- Successful payment marks the order `PAID`.
- Successful payment issues tickets.
- Successful payment creates an outbox event.
- Business transactions must not publish Kafka directly.

### Inventory Consistency

Inventory is represented by:

- `totalQuantity`
- `availableQuantity`
- `reservedQuantity`
- `soldQuantity`

All entities extend `BaseEntity`, which includes a JPA `@Version` field. Inventory consistency relies on transaction boundaries plus optimistic locking. Preserve this behavior when changing reservation, order, payment, or ticket flows.

### Security Rules

Authentication:

- JWT access token
- JWT `jti` claim
- Redis-backed active token tracking

Authorization roles:

- `ADMIN`
- `CUSTOMER`

ADMIN permissions:

- venue management
- event management
- ticket type management
- inventory management
- user administration

CUSTOMER permissions:

- reservation operations
- order operations
- payment operations
- ticket operations

### Token Revocation Strategy

Implemented:

- JWT contains `jti`.
- Redis stores active tokens.
- Redis stores user-token relationships.
- Logout revokes the current token immediately.
- User deactivation revokes all active tokens immediately.

Current Redis structures:

```text
active-token:{jti}
user-tokens:{userId}
```

Refresh tokens are implemented with opaque random tokens stored in Redis.

### Outbox Pattern Rules

Business transactions must never publish Kafka directly.

Flow:

```text
Business Transaction
  -> Outbox Event
  -> Outbox Publisher Scheduler
  -> Kafka
```

Benefits:

- transactional consistency
- retry support
- failure recovery

Use `OutboxEventService` from business services that need to publish integration events.

### Kafka Consumer Rules

Consumers must be idempotent. Duplicate Kafka messages must not create duplicate business effects.

Use `processed_kafka_events` for duplicate detection.

The current consumer handles `PAYMENT_SUCCEEDED` and creates notification records.

### Database Rules

Flyway manages schema.

Because local development frequently recreates the database:

- Modifying existing migrations is acceptable during the learning phase.
- Avoid creating unnecessary `ALTER` migrations when the user is still iterating locally.
- For production-like migration practice, create new migration versions.

JPA uses `ddl-auto: validate`, so entity changes must match Flyway migrations.

### Coding Style

Prefer:

- constructor injection
- immutable DTOs with Java records
- enums for state transitions
- explicit business exceptions
- meaningful `ErrorCode` values
- `@Transactional` at service boundaries
- repository methods over ad hoc persistence logic

Avoid:

- field injection
- static service access
- business logic inside controllers
- generic `RuntimeException`
- publishing Kafka directly from domain services
- changing unrelated modules while implementing a focused feature

### Testing Expectations

Business-critical flows should have tests:

- registration
- login
- logout
- token revocation
- reservation creation
- reservation concurrency
- order creation
- payment processing
- outbox publishing
- idempotent Kafka consumption

Prefer focused unit tests before broader integration tests. Use integration tests when transaction behavior, persistence, optimistic locking, Redis, Kafka, or Flyway behavior matters.

### Current Known Follow-up

Refresh token support exists. The next security follow-ups are:

- Review ownership checks for direct order and ticket lookup endpoints.
- Decide whether logout should revoke only the current session or all sessions.
- Add deeper auth tests only when the user explicitly prioritizes tests.
