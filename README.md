# Seat Happens

Seat Happens is a modern event ticketing backend application built with Spring Boot.

The project focuses on learning and applying real-world backend engineering concepts such as:

- Domain-Driven Design (DDD)
- Event-driven architecture
- Optimistic locking
- Inventory consistency
- Reservation lifecycle management
- Payment flow orchestration
- Scheduler-based expiration
- Transaction management
- PostgreSQL + Flyway migrations
- Dockerized local development

## Tech Stack

- Java 26
- Spring Boot 4
- PostgreSQL
- Flyway
- Docker
- Spring Data JPA
- Hibernate
- Maven
- Swagger / OpenAPI
- Spring Security
- OAuth2 Resource Server
- JWT
- Redis
- Kafka
- Kafka UI
- JavaMailSender
- Mailtrap SMTP
- MongoDB
- Elasticsearch
- Logstash
- Kibana

---

# Application Flow

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
                                    -> Ticket Ownership
```

# Features

### Authentication & Authorization

 - User registration
 - User login
 - BCrypt password hashing
 - JWT-based authentication
 - Role-based authorization
 - CUSTOMER and ADMIN roles
 - Protected API endpoints
 - User deactivation

### Token Management

 - JWT access token generation
 - JWT `jti` claim usage
 - Refresh token generation
 - Refresh token rotation
 - Redis-backed active token storage
 - Redis-backed refresh token storage
 - Logout revokes current access token and linked refresh token
 - Token revocation on user deactivation
 - Stateless authentication with server-side revocation support

### Venue Management
 - Create venue
 - List venues
 - Get venue by id

### Event Management
 - Create event
 - List events
 - List events by venue

### Ticket Type Management
 - Create ticket type
 - Automatic inventory creation
 - List ticket types by event

### Inventory Management
 - Available quantity
 - Reserved quantity
 - Sold quantity
   
Inventory consistency is protected with optimistic locking.

### Reservation Flow

- Reserve tickets
- Reservation expiration
- Reservation cancellation
- Reservation ownership protection
- Prevent overselling
- Optimistic locking support

Lifecycle: ACTIVE -> CONVERTED -> EXPIRED -> CANCELLED

### Order Flow

- Convert reservation to order
- Payment pending lifecycle
- Automatic order expiration

Order lifecycle: PENDING_PAYMENT -> PAID -> EXPIRED

### Payment Flow

- Simulated payment processing
- Payment success/failure flows
- Payment ownership protection
- Inventory finalization
- Kafka event publishing
- Outbox pattern integration

Payment success publishes `PAYMENT_SUCCEEDED` event to Kafka asynchronously.

### Ticketing

- Automatic ticket issuance
- Unique ticket code generation
- List tickets by order
- Ticket ownership protection
- QR code generation
- Ticket validation / scanning simulation

### Observability

- Correlation ID per HTTP request
- `X-Correlation-Id` request/response header
- Request logging with method, path, status, duration, user id, and correlation id
- Correlation ID propagation into outbox and Kafka event envelopes
- JSON log shipping to Logstash
- Centralized log search in Kibana

### Audit Logging

- Business audit events
- Kafka-backed audit pipeline
- MongoDB audit log persistence
- `occurredAt` timestamp with local timezone offset for readable audit review

### Notification Administration

- List notifications
- Filter notifications by status
- Get notification by id
- Retry failed notifications

---

# Kafka & Outbox Architecture
The project uses the Outbox Pattern for reliable event publishing.

```text
Flow:

Payment Success
    -> Outbox Event Created
        -> Kafka Publisher Scheduler
            -> Kafka Topic
                -> Kafka Consumer
                    -> Notification Creation
```

### Implemented concepts:

 - Reliable event publishing
 - Retry mechanism
 - Backoff strategy
 - Idempotent consumers
 - Duplicate message protection
 - Processed event tracking

---

# Notification Simulation

Kafka consumers create notification records and a scheduler sends email notifications when email delivery is enabled.

This mimics real-world systems such as:

- Email services
- SMS services
- Push notification systems
- Slack/Discord integrations
- Audit logging pipelines

Payment success notifications can be delivered through SMTP with ticket QR PNG attachments.

Email delivery is disabled by default. To use Mailtrap or another SMTP sandbox, provide SMTP settings through environment variables and enable notification email delivery:

```text
NOTIFICATION_EMAIL_ENABLED=true
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=<mailtrap-username>
MAIL_PASSWORD=<mailtrap-password>
```

---

# Kafka Concepts Practiced

The project currently demonstrates:

- Producer / Consumer architecture
- Consumer groups
- Topic-based messaging
- Offset management
- Retry handling
- At-least-once delivery
- Idempotent consumers
- Event envelopes
- Correlation ID propagation
- Asynchronous processing
- Event-driven communication

# Redis Usage

Redis is used for active JWT token tracking and token revocation.

`docker exec -it seathappens-redis redis-cli keys '*'`

When a user logs in:

```text
JWT is generated
JWT jti is stored in Redis
active-token:{jti} -> userId
user-tokens:{userId} -> list of active token ids
refresh-token:{refreshToken} -> userId
user-refresh-tokens:{userId} -> list of active refresh tokens
access-refresh-token:{jti} -> refreshToken
```

When a user is deactivated:

```text
All active access and refresh tokens of the user are removed from Redis
Existing JWTs become invalid immediately
```
This allows the application to keep JWT authentication mostly stateless while still supporting immediate token revocation.

# Local Development
Start Infrastructure by `docker compose up -d`

Infrastructure includes:

- PostgreSQL
- Kafka
- Kafka UI
- Redis
- MongoDB
- Elasticsearch
- Logstash
- Kibana

Kafka UI: http://localhost:8085

MongoDB: mongodb://localhost:27017/seathappens_audit

Elasticsearch: http://localhost:9200

Kibana: http://localhost:5601

Logstash TCP input: localhost:5001

Swagger UI: http://localhost:8080/swagger-ui.html

### ELK Local Usage

Start ELK infrastructure:

```text
docker compose up -d elasticsearch logstash kibana
```

The application sends structured JSON logs to Logstash over TCP.

In Kibana, create a data view:

```text
seathappens-logs-*
```

Useful fields:

- `correlationId`
- `userId`
- `logger_name`
- `level`
- `message`
- `method`
- `path`
- `status`
- `durationMs`

### Database Migrations

Flyway is used for schema versioning.

Migration files are located under: `src/main/resources/db/migration`

---
