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

---

# Current Flow

```text
Venue
    -> Event
        -> TicketType
            -> Inventory
                -> Reservation
                    -> Order
                        -> Payment
                            -> Ticket