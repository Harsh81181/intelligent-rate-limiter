# Day 1: Redis Setup & Connection Test

**Date:** 12-Dec-2025  
**Branch:** `feature/day1-setup`  
**Objective:** Setup Redis environment and test connectivity with Spring Boot.

---

## ✅ Tasks Completed

1. **Docker Redis Setup**
   - Installed Redis using Docker.
   - Started container named `redis-local` on port `6379`.
   - Verified running container:
     ```bash
     docker ps
     ```

2. **Spring Boot Redis Integration**
   - Added `spring-boot-starter-data-redis-reactive` dependency in `pom.xml`.
   - Configured `LettuceConnectionFactory` and `ReactiveStringRedisTemplate`.
   - Verified reactive Redis connection.

3. **Test Redis Connection**
   - Created a simple test controller `TestRedisController.java`.
   - Tested storing and retrieving a key:
     ```bash
     POST /redis/set/hello/redis
     GET /redis/get/hello
     ```
   - Output confirmed successful connection: `hello redis!`

---

## ⚡ Key Learnings

- Reactive Redis template uses `StringRedisSerializer` internally — no need to set manually.  
- Redis commands can be tested directly using `docker exec -it redis-local redis-cli ping` → returns `PONG`.  
- Docker-based Redis avoids local installation issues on Windows.

---

## 🔹 Next Steps (Day 2)

- Implement **RateLimiterService** with sliding window algorithm using Redis sorted sets.  
- Create a test API for rate limiting per user/API key.  
- Prepare feature branch `feature/day2-setup` for ongoing development.

---

## 📌 Notes

- Branch strategy for project:
  - `main` → stable, production-ready branch  
  - `dev` → integration of features  
  - `feature/dayX-setup` → daily development branch  

- Day-1 work has been pushed to GitHub on `feature/day1-setup`.

