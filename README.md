# Rate Limiter - Dockerized

This branch contains the **Dockerized version** of the Intelligent Rate Limiter project built with Spring Boot and Redis. It allows easy deployment using Docker and Docker Compose.

---

## **Project Overview**

The Rate Limiter service provides:

* **Fixed-window and sliding-window request limiting** using Redis.
* **Fail-open mechanism** when Redis is unavailable.
* **Config-driven design** for request limits, window size, and excluded paths.
* **API-level enforcement** via a Spring Boot filter.
* **Metrics tracking** using Spring Boot Actuator.

---

## **Architecture Diagram**

```
          ┌──────────────┐
          │  Client/API  │
          └──────┬───────┘
                 │
                 ▼
          ┌──────────────┐
          │ Spring Boot  │
          │ RateLimiter  │
          └──────┬───────┘
                 │
   ┌─────────────┴─────────────┐
   │                           │
   ▼                           ▼
Redis (ZSET per key)     Actuator / Metrics
```

---

## **Getting Started**

### **Prerequisites**

* Docker ≥ 20.x
* Docker Compose ≥ 1.29.x
* Optional: Java 21 (for local builds)

---

### **Clone the Repository**

```bash
git clone -b main_docker https://github.com/<your-username>/rate_limiter.git
cd rate_limiter
```

---

### **Environment Variables / Properties**

`application.properties` / `docker-compose.yml` defines all configuration:

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
spring.data.redis.host=redis
spring.data.redis.port=6397
```

---

### **Docker Setup**

#### **docker-compose.yml**

The service includes:

* **Redis container** with persistence enabled
* **Spring Boot app container** connected to Redis

#### **Build and Run**

```bash
# Build images
docker-compose build

# Start containers in detached mode
docker-compose up -d
```

Check logs:

```bash
docker-compose logs -f rate-limiter
```

#### **Stop containers**

```bash
docker-compose down
```

---

### **Access the Service**

* API endpoints: `http://localhost:8080/test`
* Actuator metrics: `http://localhost:8080/actuator/metrics`
* Redis health: verified via container network (`docker exec -it rate-limiter-redis redis-cli ping`)

---

### **DockerHub Image (Optional)**

```bash
docker pull harsh052/rate-limiter:1.0
docker run -p 8080:8080  harsh052/rate-limiter:1.0
```

---

### **Features**

| Feature             | Description                                           |
| ------------------- | ----------------------------------------------------- |
| Rate Limiting       | Sliding window with Redis ZSET                        |
| Fail-Open Mechanism | Requests allowed if Redis unavailable                 |
| Config-Driven       | `application.properties` and env variables            |
| Metrics             | Exposed via `/actuator/metrics`                       |
| Path Exclusions     | Configure excluded paths like `/swagger`, `/actuator` |

---

### **Notes**

* Ensure Docker containers are on the same network (handled automatically via Docker Compose).
* Health checks ensure the Spring Boot app waits for Redis to be ready.
* Fail-open logic ensures API requests are not blocked if Redis is down.

---
