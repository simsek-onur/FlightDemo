# FlightDemo (Quarkus)

FlightDemo is a Quarkus service that ingests flight events (Kafka) and stores/updates them in PostgreSQL.  
It also exposes REST endpoints to query and update flights, includes DB migrations (Liquibase), auditing (Hibernate Envers),
health checks, Swagger/OpenAPI, and Redis-backed caching for `GET /flight/flight-id/{flightId}`.

---

## Tech Stack
- Java 17, Quarkus
- PostgreSQL + Hibernate ORM (Panache)
- Liquibase (migrations)
- Hibernate Envers (auditing)
- Kafka (consumer: upsert by `flightId`)
- Redis cache (cache name: `flight-by-id`)
- SmallRye Health
- OpenAPI + Swagger UI

---

## Requirements
- **Java 17** (required)
- Maven
- Docker + Docker Compose

Verify:
```bash
java -version
mvn -v
