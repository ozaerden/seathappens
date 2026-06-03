# ARCHITECTURE.md

## Overview

Seat Happens is a Spring Boot modular monolith for event ticketing. It models the lifecycle from venue and event setup through ticket type inventory, reservation, order, payment, ticket issuance, and notification creation.

The application is intentionally built as a monolith with clear business modules so it can later evolve toward microservices without forcing distributed-system complexity too early.

## Technology Stack

- Java 26
- Spring Boot 4.0.6
- Maven
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Spring Security
- OAuth2 Resource Server
- JWT
- Redis
- Kafka
- Springdoc OpenAPI
- Docker Compose

## Runtime Infrastructure

Local infrastructure is defined in `docker-compose.yml`:

- PostgreSQL on `localhost:5432`
- Redis on `localhost:6379`
- Kafka on `localhost:9092`
- Kafka UI on `localhost:8085`

Application config lives in `src/main/resources/application.yaml`.

## Package Structure

Root package:

```text
com.seathappens
```

Business modules:

```text
auth
common
event
inventory
notification
order
outbox
payment
reservation
security
ticket
tickettype
user
venue
```

Most business modules follow this shape:

```text
controller
dto.request
dto.response
entity
repository
service
```

Modules add specialized packages where needed:

- `scheduler` for reservation and order expiration jobs
- `publisher` and `consumer` for outbox and Kafka integration
- `config` for module-specific properties and beans
- `filter` for security filters

## Main Business Flow

```text
Venue
  -> Event
    -> TicketType
      -> Inventory
        -> Authenticated User
          -> Reservation
            -> Order
              -> Payment
                -> Ticket
                  -> Notification
```

## Domain Model

### Venue

Venues represent event locations.

Key fields:

- name
- city
- country
- capacity
- status

### Event

Events belong to venues.

Key fields:

- venue
- name
- description
- start date time
- end date time
- status

### Ticket Type

Ticket types belong to events and define sellable ticket categories.

Key fields:

- event
- name
- description
- price
- total quantity
- status

Creating a ticket type automatically creates an inventory row.

### Inventory

Inventory is one-to-one with ticket type.

Key fields:

- total quantity
- available quantity
- reserved quantity
- sold quantity

Reservation and payment flows mutate inventory inside transactions. Optimistic locking protects concurrent updates.

### Reservation

Reservations belong to users and ticket types.

Lifecycle:

```text
ACTIVE -> CONVERTED -> EXPIRED -> CANCELLED
```

Creating a reservation:

- verifies inventory availability
- decreases `availableQuantity`
- increases `reservedQuantity`
- links the reservation to the current user
- sets an expiration timestamp

Cancelling or expiring a reservation releases inventory.

### Order

Orders belong to users and reservations.

Lifecycle:

```text
PENDING_PAYMENT -> PAID -> EXPIRED
```

Creating an order:

- loads the reservation
- requires reservation status `ACTIVE`
- verifies current-user ownership
- calculates total amount from ticket price and quantity
- marks reservation `CONVERTED`
- creates a pending-payment order

Expiring a pending order releases reserved inventory and marks both order and reservation expired.

### Payment

Payments belong to orders.

Lifecycle:

```text
SUCCESS / FAILED
```

Processing a successful payment:

- requires order status `PENDING_PAYMENT`
- marks payment `SUCCESS`
- marks order `PAID`
- moves inventory from reserved to sold
- issues tickets
- writes a `PAYMENT_SUCCEEDED` outbox event

Processing a failed payment:

- records payment as `FAILED`
- leaves the order payable until it expires

### Ticket

Tickets belong to users, orders, and ticket types.

Tickets are issued automatically after successful payment. Each ticket receives a unique generated ticket code.

Ticket access is ownership-aware:

- Admin can inspect all tickets.
- Customer can inspect only their own tickets.
- Ticket QR codes are visible only to the ticket owner or admin.

QR ticketing is implemented as a PNG QR code endpoint. Ticket validation is simulated by an admin-only scan endpoint that moves tickets from `ISSUED` to `USED`.

### Notification

Notifications are persisted database records that can be delivered as SMTP emails.

The Kafka consumer creates notification records for `PAYMENT_SUCCEEDED` events. A separate notification email scheduler processes `CREATED` notifications, sends HTML email through `JavaMailSender`, attaches ticket QR PNG files, and moves notifications to `SENT` or eventually `FAILED` after retries.

## Persistence

All entities extend `BaseEntity`, which provides:

- UUID id
- created timestamp
- updated timestamp
- JPA optimistic lock version

Schema is managed by Flyway migrations in:

```text
src/main/resources/db/migration
```

JPA is configured with:

```text
ddl-auto: validate
open-in-view: false
```

This means Flyway migrations and JPA entities must stay aligned.

## Transactions

Business write operations are generally transactional at the service layer.

Important transaction boundaries:

- `ReservationService.createReservation`
- `ReservationService.cancelReservation`
- `ReservationService.expireReservations`
- `OrderService.createOrder`
- `OrderService.expirePendingPaymentOrders`
- `PaymentService.processPayment`
- `TicketService.issueTickets`
- `NotificationService.createPaymentSucceededNotification`

Keep cross-module business invariants inside a single transaction when consistency is required.

## Concurrency

Optimistic locking is implemented through `BaseEntity.@Version`.

Inventory consistency relies on:

- transactional reads and writes
- versioned entities
- conflict handling in `GlobalExceptionHandler`

The test suite includes a reservation concurrency test that verifies two simultaneous reservations cannot oversell a one-ticket inventory.

## Security Architecture

The app uses Spring Security with stateless JWT authentication.

Public endpoints:

- `/api/auth/register`
- `/api/auth/login`
- Swagger/OpenAPI endpoints

Admin endpoints:

- `/api/venues/**`
- `/api/events/**`
- `/api/ticket-types/**`
- `/api/inventories/**`
- `/api/users/**`

Customer or admin endpoints:

- `/api/reservations/**`
- `/api/orders/**`
- `/api/payments/**`
- `/api/tickets/**`

JWT claims include:

- subject as user id
- `jti`
- email
- role
- issued-at
- expiration

`ActiveTokenFilter` checks Redis to ensure a JWT is still active.

## Redis Token Store

Redis is used to revoke JWTs while keeping authentication mostly stateless.

Keys:

```text
active-token:{jti} -> userId
user-tokens:{userId} -> set of jtis
```

Login stores token state. Logout removes the current token. User deactivation removes all active tokens for that user.

## Outbox Architecture

Kafka publishing is decoupled from business transactions using the outbox pattern.

Flow:

```text
Payment Success
  -> Outbox Event Created
  -> Outbox Publisher Scheduler
  -> Kafka Topic
  -> Kafka Consumer
  -> Notification Creation
```

Outbox event fields include:

- aggregate type
- aggregate id
- event type
- payload
- status
- published timestamp
- retry count
- last error
- next retry timestamp

The publisher scheduler sends pending events to Kafka and marks them published. Failures are retried with backoff until the max retry count is reached.

## Kafka Event Envelope

Published Kafka messages use an envelope:

```json
{
  "eventId": "...",
  "eventType": "...",
  "aggregateType": "...",
  "aggregateId": "...",
  "payload": "..."
}
```

The payload is serialized JSON stored as a string inside the envelope.

## Consumer Idempotency

Kafka is treated as at-least-once delivery. Consumers must be idempotent.

The current consumer checks `processed_kafka_events` before applying business effects. If an event id was already processed, the consumer skips it.

Notification creation also checks for an existing notification by event id.

## Error Handling

Errors are centralized in `GlobalExceptionHandler`.

Main categories:

- `ResourceNotFoundException` -> 404
- `BusinessException` -> 409
- validation errors -> 400
- infrastructure errors -> 500
- optimistic locking conflicts -> 409
- generic unexpected errors -> 500

Error codes are declared in `ErrorCode`.

## API Versioning

Controller mappings use Spring's `version = "1"` mapping style. API versioning config lives in `common.config`.

Preserve this style when adding endpoints.

## Current Architectural Boundary

The system should keep evolving as a modular monolith. Future microservice extraction candidates are:

- Identity Service
- Ticketing Service
- Payment Service
- Notification Service

Do not extract them until the monolith boundaries, tests, and operational patterns are mature.
