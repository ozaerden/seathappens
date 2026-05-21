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
 - Redis-backed active token storage
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
- Inventory finalization
- Kafka event publishing
- Outbox pattern integration

Payment success publishes `PAYMENT_SUCCEEDED` event to Kafka asynchronously.

### Ticketing

- Automatic ticket issuance
- Unique ticket code generation
- List tickets by order

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

Kafka consumers currently simulate downstream services by creating notification records.

This mimics real-world systems such as:

 - Email services
- SMS services
- Push notification systems
- Slack/Discord integrations
- Audit logging pipelines

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
- Asynchronous processing
- Event-driven communication

# Local Development
Start Infrastructure by `docker compose up -d`

Infrastructure includes:

- PostgreSQL
- Kafka
- Kafka UI
- Redis

Kafka UI: http://localhost:8085

Swagger UI: http://localhost:8080/swagger-ui.html

### Database Migrations

Flyway is used for schema versioning.

Migration files are located under: `src/main/resources/db/migration`

---

# Next Phase

Planned improvements:

 - Refresh token flow
 - Logout endpoint
 - Email Simulation
 - QR Code Generation
 - ELK / Observability
 - MongoDB Audit Logging
 - Distributed Microservice Architecture
 - Kubernetes Deployment
 - CI/CD Pipelines
 - API Gateway
 - Reactive programming
 - Mono / Flux
 - Rate limiter
 - Circuit breaker
 - Token validation at gateway layer
