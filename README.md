# Event Ledger

## Overview

Event Ledger is a microservices-based transaction processing system implemented using Spring Boot.

The solution consists of two independently deployable services:

* **Event Gateway Service**
* **Account Service**

The system supports:

* Transaction event submission
* Account balance updates
* Duplicate event handling (idempotency)
* Distributed tracing across services
* Structured logging
* Health monitoring
* Metrics and observability
* Service resiliency using Circuit Breaker
* Docker-based execution

---

# Architecture Overview

## Event Gateway Service

Responsibilities:

* Receives incoming transaction events
* Validates request payload
* Detects duplicate events
* Generates and propagates trace IDs
* Calls Account Service synchronously
* Applies resiliency policies
* Stores event data

Main APIs:

```http
POST /events
GET /events/{id}
GET /events?account={accountId}
GET /health
```

Database:

```text
H2 Embedded Database
```

---

## Account Service

Responsibilities:

* Receives transaction requests
* Updates account balances
* Recalculates balances
* Stores transaction history
* Logs propagated trace IDs
* Exposes metrics and health information

Main APIs:

```http
POST /accounts/transactions
GET /accounts/{accountId}/balance
GET /health
```

Database:

```text
H2 Embedded Database
```

---

## Service Interaction

```text
Client
  │
  │ HTTP
  ▼

Event Gateway

  │
  │ REST + traceId
  ▼

Account Service

  │
  ▼

H2 Database
```

Processing Flow:

1. Client submits event to Gateway
2. Gateway generates trace ID
3. Gateway validates and checks duplicate event
4. Gateway calls Account Service
5. Account Service updates balance
6. Response returned to client

---

# Technology Stack

Language:

```text
Java 21
```

Framework:

```text
Spring Boot
```

Database:

```text
H2 Database
```

Communication:

```text
REST API
```

Containerization:

```text
Docker
Docker Compose
```

Resiliency:

```text
Resilience4j Circuit Breaker
```

Tracing:

```text
Trace ID propagation using HTTP headers
```

Observability:

```text
Actuator
Structured Logging
Metrics
```

Testing:

```text
JUnit
Spring Boot Test
Mockito
```

---

# Setup Instructions

## Prerequisites

Install:

* Java 21
* Maven 3.9+
* Docker Desktop
* Git

Verify installation:

```bash
java -version

mvn -version

docker --version

docker compose version
```

---

# Install Dependencies

Clone repository:

```bash
git clone <repository-url>

cd event-ledger
```

Build projects:

```bash
cd event-gateway

mvn clean package
```

```bash
cd ../account-service

mvn clean package
```

---

# Start Services

## Option 1 — Docker Compose (Recommended)

Build and start:

```bash
docker compose up --build
```

Stop:

```bash
docker compose down
```

Service URLs:

Gateway:

```text
http://localhost:8080
```

Account Service:

```text
http://localhost:8081
```

---

## Option 2 — Manual Start

Terminal 1:

```bash
cd event-gateway

mvn spring-boot:run
```

Terminal 2:

```bash
cd account-service

mvn spring-boot:run
```

---

# Health Checks

Gateway:

```http
GET http://localhost:8080/health
```

Account:

```http
GET http://localhost:8081/health
```

Example:

```json
{
  "status":"UP",
  "database":"CONNECTED"
}
```

---

# Distributed Tracing

Trace propagation implemented without OpenTelemetry.

Implementation:

* Gateway generates trace ID
* Trace ID stored in MDC
* Trace ID propagated through HTTP headers
* Account Service reads trace ID
* Both services include trace ID in logs

Example:

```text
traceId=9d83d4

Gateway
↓

Account Service
```

---

# Observability

## Structured Logging

Logs contain:

* Timestamp
* Log level
* Trace ID
* Service name

Example:

```json
{
 "timestamp":"",
 "level":"INFO",
 "traceId":"abc123",
 "service":"gateway"
}
```

---

## Metrics

Implemented custom metrics:

Gateway:

```text
events.submitted
```

Account Service:

```text
account.transactions
```

Access:

```text
/actuator/metrics
```

Example:

```text
/actuator/metrics/events.submitted
```

---

# Running Tests

Run all tests:

```bash
mvn test
```

Gateway tests:

```bash
cd event-gateway

mvn test
```

Account tests:

```bash
cd account-service

mvn test
```

Covered scenarios:

* Event submission
* Duplicate event handling
* Account transaction processing
* Balance calculation
* Health endpoints
* Trace propagation
* Integration testing

---

# Resiliency Pattern Choice

Implemented:

## Circuit Breaker (Resilience4j)

Reason:

Gateway depends on Account Service.

Circuit Breaker protects the system from repeated failures and improves availability.

Behavior:

```text
Gateway

↓

Call Account Service

↓

Failure

↓

Circuit Opens

↓

Fallback

↓

503 SERVICE_UNAVAILABLE
```

Fallback:

* Prevents repeated downstream failures
* Returns controlled response
* Supports graceful degradation

---

# Constraints Followed

* Language: Java
* Database: Separate H2 DB per service
* Communication: Synchronous REST
* Tracing: Trace ID propagation
* Docker Compose supported
* Spring Boot framework

---

# Repository Submission

Repository includes:

* Source code
* Docker configuration
* README
* Tests

Commit history reflects implementation progress and is preserved without squashing.

Example:

```text
feature/gateway

feature/account

feature/docker

feature/observability
```

---

# Future Improvements

* OpenTelemetry integration
* Prometheus + Grafana
* Kubernetes deployment
* Distributed cache
* Queue-based communication
