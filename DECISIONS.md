# DECISIONS.md

## Decision: Modular Monolith

Chosen:

```text
Modular Monolith
```

Instead of:

```text
Microservices
```

Reason:

The goal is to learn business architecture first and distribute later. The current codebase should remain simple enough to run locally while still preserving clear module boundaries.

## Decision: PostgreSQL

Chosen:

```text
PostgreSQL
```

Reason:

- transactional consistency
- optimistic locking support
- production-ready relational model
- strong fit for order, payment, reservation, and inventory consistency

## Decision: Flyway

Chosen:

```text
Flyway
```

Reason:

- schema versioning
- reproducible environments
- explicit database evolution
- compatibility with `ddl-auto: validate`

## Decision: Outbox Pattern

Chosen:

```text
Outbox Pattern
```

Reason:

Kafka publishing must not break business transactions. Business services write durable outbox rows in the same transaction as domain changes. A scheduler publishes those rows asynchronously.

Benefits:

- reliability
- retry support
- recovery support
- transactional consistency

## Decision: Kafka

Chosen:

```text
Kafka
```

Reason:

Practice:

- event-driven architecture
- asynchronous communication
- producer-consumer model
- consumer groups
- offsets
- retry handling
- at-least-once delivery

## Decision: Envelope Events

Chosen structure:

```json
{
  "eventId": "...",
  "eventType": "...",
  "aggregateType": "...",
  "aggregateId": "...",
  "payload": "..."
}
```

Reason:

Consumers can process generic event metadata without needing to know the original database row. The actual domain payload remains inside `payload`.

## Decision: Idempotent Consumer

Chosen:

```text
processed_kafka_events table
```

Reason:

Kafka provides at-least-once delivery. Duplicate messages must be safely ignored.

The notification flow also checks existing notifications by event id.

## Decision: JWT Authentication

Chosen:

```text
JWT Access Tokens
```

Reason:

- stateless authentication
- industry standard
- gateway compatible
- easy role propagation through token claims

## Decision: Redis-backed Revocation

Chosen:

```text
JWT + Redis
```

Instead of:

```text
Pure stateless JWT
```

Reason:

Immediate logout and user deactivation support are required. Pure stateless JWT would remain valid until expiration.

## Decision: Role-Based Authorization

Roles:

- `ADMIN`
- `CUSTOMER`

Reason:

Simple and scalable authorization model for the current learning phase.

## Decision: Immediate User Deactivation

Chosen:

```text
Deactivate user + revoke all active tokens
```

Reason:

A deactivated user must lose access immediately.

Implementation:

- set user status to `INACTIVE`
- remove active token keys from Redis
- remove the user's token set from Redis

## Decision: Current Notification Strategy

Chosen:

```text
Database persistence + SMTP delivery
```

Instead of:

```text
Database-only simulation
```

Reason:

The event-driven flow is now stable enough to practice real SMTP delivery with Mailtrap while keeping notification state durable.

Current behavior:

- Kafka consumer creates notification records.
- Notification email scheduler sends `CREATED` notifications.
- Email content is HTML.
- Ticket QR PNG files are attached when tickets exist for the order.
- Notifications move to `SENT` on success.
- Notifications retry and eventually move to `FAILED` on repeated failures.
- SMTP credentials are configured externally and are not hardcoded.

## Decision: Kafka-backed MongoDB Audit Logging

Chosen:

```text
Business action -> Kafka audit event -> MongoDB audit document
```

Instead of:

```text
Business service writes directly to MongoDB
```

Reason:

- Main business flow does not wait for MongoDB writes.
- Kafka keeps audit events when MongoDB is temporarily unavailable.
- Audit persistence is asynchronous and replayable.
- Audit consumers can scale independently.
- This matches the existing event-driven learning path.

Current audited actions:

- user registration
- user login
- access token refresh
- user logout
- payment processing
- ticket validation

## Decision: Refresh Tokens

Current status:

```text
Implemented
```

Reason:

The access-token-only flow was sufficient for the first security learning phase. Refresh token flow was added as the next security enhancement.

Chosen:

```text
Opaque random refresh tokens stored in Redis
```

Current behavior:

- login returns access token and refresh token
- refresh endpoint rotates refresh tokens
- old refresh token is revoked after refresh
- logout revokes the current access token and its linked refresh token
- user deactivation revokes all known access and refresh tokens

Open design questions:

- single-session logout or all-session logout
- whether to add auth integration tests later

## Known Technical Debt

1. ELK integration not implemented.
2. API Gateway not implemented.
3. Dedicated production email provider integration is not implemented yet.
4. No distributed deployment strategy yet.
5. A dedicated staff role for ticket scanning is not implemented yet.
6. Admin audit search endpoints are not implemented yet.

## Known Non-Issues

The following are intentional:

- modular monolith architecture
- Flyway migration editing during local learning
- notification persistence as the durable source for email delivery
- Redis token revocation approach
- Kafka consumer idempotency table
- Kafka-backed MongoDB audit logging
- keeping microservice extraction as a future step
