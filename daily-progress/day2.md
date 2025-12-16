# Day 2: Sliding Window Rate Limiter (Core + Hardening)

**Date:** Day 2  
**Project:** Intelligent Rate Limiter Platform  
**Focus:** Core algorithm, production readiness, verification

---

## 🎯 Objective

Implement a distributed rate limiter using Redis and make it production-ready by adding:
- clear algorithm design
- structured logging
- Redis key strategy
- unit testing
- runtime verification

---

## ✅ Work Completed

---

### 1. Implemented Core Rate Limiter Logic

- Built a **Sliding Window Log Rate Limiter** using Redis **Sorted Sets (ZSET)**.
- Each request is stored with its timestamp.
- Requests older than the window size are removed on every call.
- The number of remaining requests decides **ALLOW / BLOCK**.

**Why Sliding Window Log?**
- Fair throttling
- Avoids fixed-window burst issues
- Works well in distributed systems

---

### 2. Redis Operations Used

| Operation | Purpose |
|---------|--------|
| `ZREMRANGEBYSCORE` | Remove expired requests |
| `ZCARD` | Count requests in window |
| `ZADD` | Add current request |
| `EXPIRE` | Prevent memory leaks |

---

### 3. Added Structured Logging

Added logs at **decision points only** (not every Redis call).
#Example logs:
INFO  RateLimiterService - Rate limit allowed | key=test-user | count=4 | limit=5
WARN  RateLimiterService - Rate limit exceeded | key=test-user | count=5 | limit=5
#Benefits:
- Debugging
- Monitoring abuse
- Production observability


### 4. Redis Key Naming Strategy

Introduced a scalable key format:
{environment}:rate-limiter:user:{identifier}
#Example:
local:rate-limiter:user:test-user
#Benefits:
- Environment isolation
- Easier debugging
- Supports future rate-limit types


### 5. Unit Testing Rate Limiter Logic

- Added unit tests to verify:
- Requests within limit → allowed
- Requests exceeding limit → blocked
- Used .block() in tests to validate reactive flows.

Test location:

src/test/java/com/harsh/ratelimiter/service/RateLimiterServiceTest.java


### 6. Runtime Verification (Manual Testing)

Verified behavior using:
- API calls
- application logs
- Redis CLI inspection

Observed behavior:
- First 5 requests → ALLOWED
- 6th request → BLOCKED
- Logs showed correct incremental counts
- Redis ZSET stored timestamps correctly

---

## 🧠 Key Learnings

- Reactive Redis APIs differ from blocking APIs (score-based ops require `Range<Double>`).
- Sliding Window Log provides accurate throttling but requires cleanup.
- Logging and key design are as important as the algorithm itself.
- Reactive code becomes readable when logic is broken into intent-based methods.
- Verification through logs + Redis CLI is critical.

---

## 📌 Status

| Item | Status |
|----|----|
| Core algorithm | ✅ |
| Logging | ✅ |
| Redis key strategy | ✅ |
| Unit tests | ✅ |
| Runtime verification | ✅ |

---

## 🔜 Next Steps (Day 3)

- Convert rate limiter logic into a **single Redis Lua script**.
- Ensure **atomic execution** under high concurrency.
- Eliminate race conditions completely.

---

**Day 2 Outcome:**  
A working, tested, and production-ready Redis-based sliding window rate limiter.