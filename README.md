# Event Ledger

## Overview

Event Ledger is a distributed microservices-based system for processing financial transaction events.

The system consists of two independently deployable services:

1. **Event Gateway Service** (public-facing)
2. **Account Service** (internal)

The solution is designed to handle:

* Idempotent event processing
* Out-of-order event delivery
* Distributed trace propagation
* Health monitoring
* Resiliency between services
* Independent service execution

---

# Architecture Overview

## Event Gateway API

Responsibilities:

* Receives transaction events
* Validates request payload
* Enforces idempotency
* Stores event records locally
* Calls Account Service to apply transactions
* Supports event retrieval APIs

Endpoints:

```http
POST /events
GET /events/{id}
GET /events?account={accountId}
GET /health
```

---

## Account Service

Responsibilities:

* Maintains account balances
* Applies transactions
* Provides balance and account information
* Stores account state independently

Endpoints:

```http
POST /accounts/{accountId}/transactions
GET /accounts/{accountId}/balance
GET /accounts/{accountId}
GET /health
```

---

## Service Interaction

```text
Client
   |
   | HTTP
   v
Event Gateway
   |
   | REST Call
   v
Account Service
```

Flow:

1. Client submits event to Gateway
2. Gateway validates request
3. Gateway stores event
4. Gateway calls Account Service
5. Account Service updates balance
6. Response returned to client

Each service maintains its own H2 database.

---

# Technology Stack

* Java 21
* Spring Boot
* Spring Data JPA
* H2 Database
* Maven
* Docker
* Docker Compose
* Resilience4j
* JUnit
* OpenTelemetry (Trace Propagation)

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

Build both services:

```bash
cd event-gateway
mvn clean install

cd ../account-service
mvn clean install
```

---

# Start Application

## Option 1 — Docker Compose (Recommended)

Build and start:

```bash
docker compose up --build
```

Application URLs:

Gateway:

```text
http://localhost:8080
```

Account Service:

```text
http://localhost:8081
```

Stop:

```bash
docker compose down
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

# Health Endpoints

Gateway:

```http
GET /health
```

Account Service:

```http
GET /health
```

Example:

```json
{
  "service":"event-gateway",
  "status":"UP",
  "database":"CONNECTED"
}
```

---

# Running Tests

Run all tests:

```bash
mvn test
```

Tests cover:

* Event validation
* Idempotency
* Out-of-order events
* Balance calculation
* Health endpoint
* Integration testing
* Trace propagation
* Resiliency scenarios

Generate reports:

```bash
mvn verify
```

---

# Resiliency Pattern Choice

This implementation uses:

## Timeout + Retry with Backoff

Reason:

The Event Gateway depends on Account Service to apply transactions.

To improve reliability:

* Timeout prevents hanging requests
* Retry handles temporary failures
* Backoff avoids overwhelming downstream services

Behavior:

```text
Gateway
   |
Call Account Service
   |
Failure
   |
Retry
   |
Timeout
   |
Return 503
```

If Account Service remains unavailable:

* POST /events returns HTTP 503
* GET event APIs continue working using Gateway local data

This provides graceful degradation while maintaining availability.

---

# Future Improvements

* Kubernetes deployment
* Prometheus metrics
* Jaeger tracing
* Circuit breaker
* Event queue fallback
* External database support
