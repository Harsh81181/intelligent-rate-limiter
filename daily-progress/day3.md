# 📅 Day 3 – Atomic Rate Limiting with Redis Lua & Observability

## 🎯 Goal of Day 3

Move from a **client-side Redis rate limiter** to a **production-grade, atomic rate limiting solution** using **Redis Lua scripting**, and add **metrics + resilience** for real-world behavior.

---

## 🚀 What I Built

### 1️⃣ Atomic Rate Limiting using Redis Lua Script

Implemented a **fixed sliding window rate limiter** using:

* Redis **Sorted Sets (ZSET)**
* **Lua scripting** for atomic execution

**Why Lua?**

* Guarantees atomicity under high concurrency
* Prevents race conditions across multiple app instances
* Executes all Redis commands as a single unit
* Reduces network round trips

**Lua script flow:**

1. Calculate window start time
2. Remove expired requests from ZSET
3. Count current requests
4. Allow or block based on limit
5. Add request timestamp if allowed
6. Set TTL on Redis key
7. Return {allowed, remaining}

---

### 2️⃣ Dedicated Lua-Based Rate Limiter Service

Introduced a separate service:

```
RateLimiterLuaService
```

**Why a new service instead of modifying the old one?**

* Clear separation of concerns
* Easier comparison between client-side vs Lua-based approaches
* Cleaner architecture and better maintainability

**Key characteristics:**

* Reactive (`ReactiveStringRedisTemplate`)
* Non-blocking Redis execution
* Maps Lua response cleanly into domain DTO

---

### 3️⃣ Environment-Aware Redis Key Strategy

Centralized Redis key generation using a key builder:

```
{environment}:rate-limiter:user:{userKey}
```

**Benefits:**

* Prevents key collisions across environments
* Makes Redis debugging easier
* Production-safe when using shared Redis clusters

---

### 4️⃣ Resilience: Fail-Open Strategy

Designed the system to **fail open** when Redis is unavailable:

* Requests are allowed if Redis is down
* Prevents cascading failures
* Matches real API gateway behavior in high-availability systems

This is a **conscious trade-off** favoring availability over strict enforcement.

---

### 5️⃣ Metrics & Observability (Micrometer + Actuator)

Added custom metrics using **Micrometer**, exposed via Spring Actuator.

**Metrics implemented:**

* `rate_limiter.allowed`
* `rate_limiter.blocked`
* `rate_limiter.fallback`

Accessible via:

```
/actuator/metrics
```

**Why this matters:**

* Helps detect abuse patterns
* Tracks Redis outages via fallback metric
* Enables alerting and monitoring in production

---

## 🧠 Engineering Decisions & Trade-offs

| Decision             | Reason                                |
| -------------------- | ------------------------------------- |
| Redis ZSET           | Efficient time-based request tracking |
| Lua scripting        | Atomic execution & concurrency safety |
| Fail-open            | Higher system availability            |
| Separate Lua service | Clean architecture                    |
| Metrics              | Production observability              |

---

## 🧪 Testing Performed

* Verified rate limiting via Postman
* Confirmed correct blocking after threshold
* Manually stopped Redis to test fallback behavior
* Validated metrics via Actuator endpoints

---

## 📂 Files Added / Updated

* `RateLimiterLuaService.java`
* `RateLimiterKeyBuilder.java`
* Redis Lua script configuration
* Metrics configuration
* Controller updates for Lua-based limiter

---

## 📈 Outcome

By the end of Day 3, the system now supports:

✅ Atomic rate limiting
✅ Safe concurrency handling
✅ Environment-aware Redis keys
✅ Graceful Redis failure handling
✅ Production-grade observability

This moves the project from **implementation-focused** to **engineering-focused**.

---

## 🔜 Next: Day 4

* System design discussion & scalability
* Config-driven limits
* Multi-instance behavior
* Architecture diagram
* Interview-ready explanations

---

💡 **Reflection**
Day 3 was less about writing more code and more about **thinking like a backend engineer working on distributed systems**.