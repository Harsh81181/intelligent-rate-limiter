# Day 4 – Config-Driven Rate Limiting & Filter-Based Enforcement

## Overview

Day 4 focused on converting the rate limiter from a controller-level implementation into a **filter-based, configuration-driven system**, closer to how real production systems enforce cross-cutting concerns like throttling, authentication, and observability.

The objective was to ensure that **every incoming request is evaluated centrally**, without duplicating logic in controllers, while keeping the system flexible, safe, and extensible.

---

## Key Objectives Achieved

### 1. Moved Rate Limiting to Servlet Filter

* Implemented rate limiting at the filter level instead of controllers.
* Ensured the controller remains clean and unaware of rate-limiting concerns.
* Prevented double invocation of rate-limiting logic by enforcing it only once in the filter chain.

Why this matters:

* Filters are the correct abstraction for cross-cutting concerns.
* This aligns with real-world API gateway and middleware designs.

---

### 2. Path Exclusion Strategy

Certain endpoints must never be rate-limited, such as:

* Actuator endpoints
* Health checks
* Metrics and monitoring endpoints
* API documentation paths

Implemented:

* A configurable exclusion list using application properties.
* Early short-circuit logic in the filter to bypass Redis entirely for excluded paths.

Benefits:

* Prevents breaking monitoring and health probes.
* Reduces unnecessary Redis load.
* Improves operational safety.

---

### 3. Config-Driven Design

All rate-limiting parameters were externalized to configuration:

* Request limit
* Sliding window duration
* Excluded paths
* Environment-based key prefixes

This allows:

* Zero code changes for tuning limits.
* Environment-specific behavior (local, staging, production).
* Better alignment with 12-factor app principles.

---

### 4. Rate Limiting Key Strategy (Production-Grade)

Implemented a hierarchical key resolution strategy:

1. X-API-KEY (if present)
2. Authenticated user identifier (future-ready)
3. Client IP address as fallback

This ensures:

* Fair rate limiting for authenticated clients.
* Better protection against abuse.
* Scalability for public APIs and partner integrations.

---

### 5. Fail-Open Behavior for Redis Failures

Handled Redis unavailability scenarios gracefully:

* If Redis is down before application startup, system allows traffic.
* If Redis goes down while the application is running, errors are handled safely.
* Requests are allowed instead of blocking the entire application.

Rationale:

* Rate limiting is a protective mechanism, not a critical dependency.
* System availability takes precedence over throttling.

---

### 6. Filter Stability and Runtime Behavior

* Resolved hanging behavior when Redis was stopped mid-runtime.
* Ensured proper error handling and timeouts in reactive Redis calls.
* Application remains responsive even during Redis outages.

---

## Final Architecture Position After Day 4

* Rate limiting enforced centrally via filter
* Redis + Lua used for atomic sliding window logic
* Configuration-driven limits and exclusions
* Fail-open resilience behavior
* Clean controller layer
* Production-aligned request handling flow

---

## Key Learnings

* Filters are the correct enforcement layer for rate limiting.
* Infrastructure endpoints must be explicitly protected from throttling.
* Configuration-driven design is mandatory for scalable backend systems.
* Fail-open strategies are essential for non-critical dependencies.
* Designing for failure is as important as designing for success.

---
