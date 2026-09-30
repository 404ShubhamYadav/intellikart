# IntelliKart

A distributed, event-driven e-commerce backend built to demonstrate real microservices patterns — SAGA orchestration with compensation, Kafka-based service communication, JWT authentication via an API Gateway, and Elasticsearch-backed search — rather than a simple CRUD monolith.

## Architecture

```
                         ┌──────────────┐
                         │   Client     │
                         └──────┬───────┘
                                │
                         ┌──────▼───────┐
                         │  API Gateway │  (Spring Cloud Gateway, JWT validation)
                         └──────┬───────┘
              ┌─────────────────┼─────────────────┐
              │                 │                 │
       ┌──────▼──────┐   ┌──────▼──────┐   ┌──────▼──────┐
       │ user-service│   │order-service│   │inventory-svc│
       │  (auth/JWT) │   │             │   │ (+ search)  │
       └─────────────┘   └──────┬──────┘   └──────┬──────┘
                                 │                 │
                          ┌──────▼─────────────────▼──────┐
                          │           Kafka                │
                          └──────┬─────────────────┬───────┘
                                 │                 │
                       ┌─────────▼────────┐ ┌──────▼──────┐
                       │orchestration-svc │ │payment-svc  │
                       │  (SAGA state)    │ │             │
                       └──────────────────┘ └─────────────┘
```

Each service owns its own MySQL database (database-per-service). Services communicate exclusively through Kafka events/commands — no direct service-to-service REST calls in the order fulfillment flow.

## Services

| Service | Responsibility | Port |
|---|---|---|
| `api-gateway` | Single entry point, request routing, JWT validation, forwards authenticated identity to downstream services | 8080 |
| `user-service` | Registration, login, JWT issuance (BCrypt password hashing) | 8085 |
| `order-service` | Order creation, publishes `OrderPlaced`, applies orchestrator confirm/cancel commands | 8081 |
| `inventory-service` | Stock management with two-pass reservation checks (verify-then-commit, avoiding partial decrements on multi-item failures); Elasticsearch-backed product search | 8082 |
| `payment-service` | Mock payment processing | 8083 |
| `orchestration-service` | SAGA orchestrator — owns saga state, issues all commands, is the sole consumer of every result event | 8084 |
| `common-lib` | Shared, framework-free library: DTOs, Kafka event/command classes, topic name constants | — |

## Key architectural decisions

- **Orchestration over choreography.** The system was deliberately built choreography-first, then refactored to a command-driven orchestrator once the limits of choreography (no central saga state, no clean compensation path) became apparent — a decision made explicitly, not by default.
- **SAGA compensation is real, not diagrammed.** A failed payment triggers a `ReleaseStockCommand` that restores reserved inventory, and a `CancelOrderCommand` that marks the order cancelled — verified end-to-end, not just described.
- **Gateway-level authentication.** `api-gateway` validates JWTs and forwards the authenticated user's identity via a trusted header; downstream services never see or trust client-supplied identity fields.
- **`common-lib` stays framework-free.** No Spring Boot dependency in the shared library, so it never forces a Spring Boot version on any consuming service.
- **Database-per-service, enforced.** No service reads another service's database directly, including `payment-service`, which stores `orderId` as a plain field rather than a foreign key relationship.

## Tech stack

Java 21, Spring Boot 4, Spring Cloud Gateway (WebMVC), Spring Data JPA, Spring Kafka, Spring Data Elasticsearch, Spring Security (JWT), MySQL 8, Apache Kafka (KRaft mode), Elasticsearch, Docker Compose, Lombok, Maven.

## Running locally

```bash
docker compose up -d
```

Then start each service (`order-service`, `inventory-service`, `payment-service`, `orchestration-service`, `user-service`, `api-gateway`) from your IDE or via `./mvnw spring-boot:run`.

### Example flow

```bash
# Register
POST http://localhost:8080/api/v1/auth/register
{ "email": "test@intellikart.com", "password": "password123", "name": "Test User" }

# Seed a product
POST http://localhost:8082/api/v1/products
{ "productId": "prod-1", "name": "T-Shirt", "description": "Cotton crew-neck", "availableQuantity": 20 }

# Place an order (Authorization: Bearer <token from register>)
POST http://localhost:8080/api/v1/orders
{ "items": [{ "productId": "prod-1", "quantity": 2 }] }
```

## Known limitations / future work

- **Authorization** is not yet fine-grained — any authenticated user can currently act on any order ID; ownership checks are a planned addition.
- **No cloud deployment yet** — currently local-only via Docker Compose.
