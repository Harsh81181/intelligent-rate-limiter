# Intelligent Rate Limiter Platform

## Overview

This project is a production-grade, Redis-backed intelligent rate limiter implemented using Spring Boot. It enforces request throttling at the application edge using a sliding window algorithm executed atomically via Redis Lua scripts. The system is designed to be configurable, resilient, observable, and suitable for real-world backend systems.

The rate limiter operates at the filter level, ensuring that requests are evaluated before reaching any business logic. It supports multiple key strategies (API key, user identifier, and IP address), exposes rate-limit metadata via HTTP headers, and follows a fail-open strategy to prioritize availability during Redis outages.

---

## Key Features

* Sliding window rate limiting using timestamps
* Atomic decision-making via Redis Lua scripts
* Servlet filter-based enforcement
* Multiple rate-limit key strategies
* Config-driven limits and exclusions
* Fail-open behavior on Redis failure
* Bounded execution with timeouts
* HTTP response headers for client awareness
* Metrics and observability via Spring Actuator

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
Controller (Only if request is allowed)
```

---

## Request Flow

1. An incoming HTTP request reaches the application.
2. The request is intercepted by `RateLimiterFilter` before controller execution.
3. A rate-limit key is resolved using the following priority:

   * X-API-KEY header
   * Authenticated user identifier (if present)
   * Client IP address
4. The resolved key is transformed into an environment-aware Redis key.
5. A Redis Lua script executes atomically to:

   * Remove expired request entries
   * Count requests in the active window
   * Decide whether the request is allowed
   * Insert the current request if allowed
   * Set TTL for automatic cleanup
6. Based on the result:

   * Allowed requests proceed through the filter chain
   * Blocked requests return HTTP 429 without invoking controllers
7. Rate-limit metadata is added to HTTP response headers.
8. Metrics and logs are emitted for observability.

---

## Rate Limiting Algorithm

### Sliding Window Strategy

* Each request timestamp is stored in a Redis sorted set (ZSET).
* The score and value of each entry represent the request timestamp.
* Before counting, expired timestamps outside the window are removed.
* The current count is compared against the configured limit.

This approach avoids burst anomalies seen in fixed window algorithms and provides accurate enforcement under concurrent traffic.

---

## Redis Data Structure

* Redis Sorted Set (ZSET)
* Key format:

  ```
  <environment>:rate-limiter:<key-type>:<identifier>
  ```

Example:

```
local:rate-limiter:api-key:abc123
```

---

## Lua Script (Atomic Enforcement)

All rate limiting logic is executed inside Redis using Lua scripting. This ensures:

* Atomic execution
* No race conditions
* Single network round-trip
* Correct behavior under high concurrency

The script performs cleanup, counting, decision-making, insertion, and TTL handling in one atomic operation.

---

## Failure Handling and Resilience

### Redis Unavailable at Startup

* Application starts successfully
* Rate limiting is bypassed
* Requests are allowed

### Redis Unavailable During Runtime

* Lua execution is bounded with a strict timeout
* Errors trigger fail-open fallback
* Requests are allowed without blocking
* Fallback metrics are incremented

### Design Rationale

The system follows an availability-first (fail-open) strategy to prevent cascading failures and ensure business continuity.

---

## Configuration

All behavior is externalized via configuration properties.

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

---

## Configuration Reference Table

| Property                          | Description                             | Default                                 |
| --------------------------------- | --------------------------------------- | --------------------------------------- |
| rate-limiter.window-ms            | Sliding window duration in milliseconds | 10000                                   |
| rate-limiter.max-requests         | Maximum allowed requests per window     | 3                                       |
| rate-limiter.excluded-paths       | Paths excluded from rate limiting       | /actuator,/swagger,/v3/api-docs,/health |
| spring.data.redis.timeout         | Redis command timeout                   | 100ms                                   |
| spring.data.redis.connect-timeout | Redis connection timeout                | 100ms                                   |

---

## HTTP Response Headers

The rate limiter adds standard headers to responses:

* X-Rate-Limiter-Limit
* X-Rate-Limiter-Remaining
* X-Rate-Limiter-Reset

These headers allow clients to implement retry and backoff strategies.

---

## Metrics and Observability

The system exposes metrics via Spring Actuator:

* rate_limiter.allowed
* rate_limiter.blocked
* rate_limiter.fallback

Metrics are accessible at:

```
/actuator/metrics
```

Logs provide structured insight into allow, block, and fallback decisions.

---

## Validation and Testing

The following scenarios have been validated:

* Burst traffic exceeding the configured limit
* Window expiration and automatic recovery
* Redis latency and timeout handling
* Redis outage with fail-open behavior

These tests confirm correctness, resilience, and bounded response times.

---

## Future Improvements

* Per-endpoint rate limits
* Token bucket or leaky bucket algorithms
* Redis cluster support
* Circuit breakers around Redis
* WebFlux-native non-blocking filter

---

## License

MIT License.

---

## Contact

For questions or collaboration:
Harsh Bhardwaj
GitHub : Harsh81181


---

## Summary

This project demonstrates a production-ready approach to rate limiting with strong emphasis on correctness, concurrency safety, resilience, and observability. It is designed to scale horizontally and operate safely under real-world failure conditions.
