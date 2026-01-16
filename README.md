# FlightDemo (Quarkus)

FlightDemo is a Quarkus service that ingests flight events (Kafka) and upserts them into PostgreSQL.
It exposes REST endpoints to query and update flights, includes DB migrations (Liquibase), auditing (Hibernate Envers),
health checks, Swagger/OpenAPI, and Redis-backed caching for `GET /flight/flight-id/{flightId}`.

## Tech Stack
- Java 17, Quarkus
- PostgreSQL + Hibernate ORM (Panache)
- Liquibase (migrations)
- Hibernate Envers (auditing)
- Kafka (Redpanda) consumer: upsert by `flightId`
- Redis cache (cache name: `flight-by-id`)
- SmallRye Health
- OpenAPI + Swagger UI
- Tests: REST Assured + Testcontainers (Postgres)

## Requirements
- Java 17 (required)
- Maven
- Docker + Docker Compose

## Quickstart (copy/paste)
```bash
# Verify Java + Maven (must be Java 17)
java -version
mvn -v

# Start dependencies (compose file lives under src/main/docker)
cd src/main/docker
docker compose up -d
docker compose ps
cd -

# Run the app
mvn quarkus:dev

# Health
curl -i http://localhost:8080/q/health/ready
curl -i http://localhost:8080/q/health/live

# Swagger / OpenAPI
open http://localhost:8080/q/swagger-ui/
curl -s http://localhost:8080/q/openapi | head -n 20

# Create a flight
curl -i -X POST "http://localhost:8080/flight" \
  -H "Content-Type: application/json" \
  -d '{
    "flightId":"TEST-FLT-1",
    "carrierCode":"TK",
    "flightNumber":123,
    "flightDate":"2026-01-16",
    "traffic":"INTERNATIONAL",
    "departureAirport":"IST",
    "arrivalAirport":"LHR"
  }'

# Get by flightId (cached)
curl -i "http://localhost:8080/flight/flight-id/TEST-FLT-1"

# Update by flightId (also invalidates cache for that flightId)
curl -i -X PUT "http://localhost:8080/flight/flight-id/TEST-FLT-1" \
  -H "Content-Type: application/json" \
  -d '{
    "flightId":"TEST-FLT-1",
    "carrierCode":"TK",
    "flightNumber":999,
    "flightDate":"2026-01-16",
    "traffic":"INTERNATIONAL",
    "departureAirport":"IST",
    "arrivalAirport":"LHR"
  }'

# List flights (paged)
curl -s "http://localhost:8080/flight?page=0&size=10"

# Verify Redis (host port 6367)
redis-cli -p 6367 PING
redis-cli -p 6367 KEYS "cache:flight-by-id:*"

# Run tests (uses Testcontainers for Postgres)
mvn test

# Stop dependencies
cd src/main/docker
docker compose down
cd -
