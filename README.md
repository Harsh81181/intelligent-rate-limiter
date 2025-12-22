# Fault Tolerant Rate Limiter Platform (Dockerized)

A **production-grade, Redis-backed Fault Tolerant Rate Limiter** built using **Spring Boot** and designed for **correctness, resilience, observability, and real-world scalability**.
This repository includes a **fully Dockerized setup** using Docker and Docker Compose for easy deployment.

---

## Overview

This service enforces request throttling at the **application edge** using a **sliding window rate limiting algorithm** executed **atomically via Redis Lua scripts**.

Rate limiting is applied at the **Servlet Filter level**, ensuring requests are evaluated **before any business logic executes**. The system supports multiple key strategies (API key, user, IP), exposes rate-limit metadata via HTTP headers, and follows a **fail-open strategy** to prioritize availability during Redis outages.

---

## Key Features

* Sliding window rate limiting using Redis ZSETs
* Atomic enforcement via Redis Lua scripts
* Servlet filter–based request interception
* Multiple rate-limit key strategies:

  * API Key
  * Authenticated user
  * Client IP
* Config-driven limits and excluded paths
* Fail-open behavior on Redis failures
* Bounded Redis execution with strict timeouts
* Standard HTTP rate-limit headers
* Metrics and observability via Spring Actuator
* Fully Dockerized deployment using Docker Compose

---

## High-Level Architecture

```
          Client
             |
             v
     RateLimiterFilter
             |
             v
 Redis Lua Script (Atomic Sliding Window)
             |
             v
        Controller
   (only if request is allowed)
```

---

## Request Flow

1. Incoming HTTP request reaches the application.
2. `RateLimiterFilter` intercepts the request before controller execution.
3. A rate-limit key is resolved in priority order:

   * `X-API-KEY` header
   * Authenticated user identifier (if available)
   * Client IP address
4. The resolved identifier is converted into an environment-aware Redis key.
5. A Redis Lua script executes atomically to:

   * Remove expired entries
   * Count active requests in the window
   * Decide allow / block
   * Insert the current request if allowed
   * Apply TTL for automatic cleanup
6. Based on the result:

   * Allowed requests continue to controllers
   * Blocked requests return **HTTP 429**
7. Rate-limit metadata is added to response headers.
8. Metrics and logs are emitted for observability.

---

## Rate Limiting Algorithm

### Sliding Window Strategy

* Each request timestamp is stored in a Redis **Sorted Set (ZSET)**.
* Timestamp acts as both score and value.
* Before counting, expired timestamps are removed.
* Current request count is compared against the configured limit.

This approach avoids burst anomalies common in fixed-window algorithms and remains accurate under concurrent traffic.

---

## Redis Data Model

* **Data Structure:** Redis Sorted Set (ZSET)
* **Key Format:**

```
<environment>:rate-limiter:<key-type>:<identifier>
```

Example:

```
local:rate-limiter:api-key:abc123
```

---

## Lua Script (Atomic Enforcement)

All rate limiting logic is executed **inside Redis** using Lua scripting, ensuring:

* Atomic execution
* No race conditions
* Single network round-trip
* Correct behavior under high concurrency

The script performs cleanup, counting, decision-making, insertion, and TTL handling in one operation.

---

## Failure Handling & Resilience

### Redis Unavailable at Startup

* Application starts successfully
* Rate limiting is bypassed
* Requests are allowed

### Redis Unavailable During Runtime

* Lua execution is bounded with strict timeouts
* Errors trigger a fail-open fallback
* Requests continue without blocking
* Fallback metrics are recorded

### Design Philosophy

The system is **availability-first** to prevent cascading failures and protect core business traffic.

---

## Configuration

All behavior is externalized via configuration.

### Application Properties

```properties
spring.application.name=rate_limiter
spring.profiles.active=local

management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.metrics.enabled=true

rate-limiter.window-ms=10000
rate-limiter.max-requests=3
rate-limiter.excluded-paths=/actuator,/swagger,/v3/api-docs,/health

spring.data.redis.timeout=100ms
spring.data.redis.connect-timeout=100ms
```

### Configuration Reference

| Property                          | Description                  | Default                                 |
| --------------------------------- | ---------------------------- | --------------------------------------- |
| rate-limiter.window-ms            | Sliding window duration (ms) | 10000                                   |
| rate-limiter.max-requests         | Max requests per window      | 3                                       |
| rate-limiter.excluded-paths       | Paths excluded from limiting | /actuator,/swagger,/v3/api-docs,/health |
| spring.data.redis.timeout         | Redis command timeout        | 100ms                                   |
| spring.data.redis.connect-timeout | Redis connection timeout     | 100ms                                   |

---

## Dockerized Setup

### Prerequisites

* Docker ≥ 20.x
* Docker Compose ≥ 1.29.x
* Optional: Java 21 (for local development)

---

### Clone the Repository

```bash
git clone -b main_docker https://github.com/Harsh81181/Fault-Tolerant-Rate-Limiter.git
cd rate_limiter
```

---

### Docker Architecture

* Spring Boot application container
* Redis container with persistence
* Isolated Docker network via Docker Compose
* Health checks ensure Redis readiness

---

### Build and Run

```bash
docker-compose build
docker-compose up -d
```

View logs:

```bash
docker-compose logs -f rate-limiter
```

Stop containers:

```bash
docker-compose down
```

---

## Access the Service

* Test endpoint:
  `http://localhost:8080/test`

* Actuator metrics:
  `http://localhost:8080/actuator/metrics`

* Redis health check:

  ```bash
  docker exec -it rate-limiter-redis redis-cli ping
  ```

---

## DockerHub Image 

```bash
docker pull harsh052/rate-limiter:1.0
docker run -p 8080:8080 harsh052/rate-limiter:1.0
```

---

## HTTP Response Headers

The rate limiter adds standard headers:

* `X-Rate-Limiter-Limit`
* `X-Rate-Limiter-Remaining`
* `X-Rate-Limiter-Reset`

These allow clients to implement retry and backoff strategies.

---

## Metrics & Observability

Exposed via Spring Actuator:

* `rate_limiter.allowed`
* `rate_limiter.blocked`
* `rate_limiter.fallback`

Available at:

```
/actuator/metrics
```

Structured logs provide insight into allow, block, and fallback decisions.

---

## Validation & Testing

Validated scenarios include:

* Burst traffic beyond configured limits
* Window expiration and recovery
* Redis latency and timeout handling
* Redis outages with fail-open behavior

---

## Future Improvements

* Per-endpoint rate limits
* Token bucket / leaky bucket algorithms
* Redis cluster support
* Circuit breakers around Redis
* WebFlux-native non-blocking filter

---

## License

MIT License

---

## Contact

**Harsh Bhardwaj**
GitHub: `Harsh81181`

---

## Summary

This project demonstrates a **production-ready rate limiting system** with strong emphasis on **correctness, concurrency safety, resilience, and observability**, packaged with a **Docker-first deployment strategy** suitable for real-world backend systems.

---
