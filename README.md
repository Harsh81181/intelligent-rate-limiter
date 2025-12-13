# 📌 **README.md – Intelligent Distributed Rate Limiter Platform**

High-performance distributed rate limiting engine implementing Token Bucket and Sliding Window algorithms with Redis. Supports dynamic rule configuration, API key management, and real-time metrics. Designed for scalable microservices and fintech-grade reliability.

# 🚀 Intelligent Distributed Rate Limiter Platform

A high-performance **distributed rate limiting engine** built with **Spring Boot + Redis**, supporting **Token Bucket** and **Sliding Window** algorithms. Designed for scalable microservice architectures and fintech-grade reliability.

---

## 🔥 Features

* ⚡ **High-Throughput Distributed Rate Limiting**
  Handles millions of requests using Redis atomic operations.

* 🪣 **Multiple Algorithms**

  * Token Bucket (burst-friendly)
  * Sliding Window (fair throttling)

* 🔑 **API Key–Based Rate Limits**
  Per-client / per-endpoint configuration.

* 🛠 **Dynamic Rule Management**
  Update rate limits at runtime without restarting the service.

* 📊 **Real-Time Metrics & Monitoring**
  Track:

  * allowed requests
  * blocked requests
  * token consumption
  * request rate

* 🧩 **Clean Microservice Architecture**
  Modular service layers, Redis-backed state, admin module.

* 🧪 **Unit-tested Core Algorithms**
  Ensures correctness under concurrency.

---

## 🏗️ **System Architecture**

```
                      ┌──────────────────────────┐
                      │      Client Services      │
                      └──────────────┬───────────┘
                                     │ /check
                             (API Key + Permits)
                                     │
                        ┌────────────▼────────────┐
                        │  Rate Limiter API Layer │
                        └────────────┬────────────┘
                                     │
                                  invokes
                                     │
                        ┌────────────▼────────────┐
                        │   Limiter Core Service   │
                        │  (TokenBucket/Sliding)   │
                        └────────────┬────────────┘
                                     │
                                uses Redis
                                     │
                ┌────────────────────▼─────────────────────┐
                │       Redis (Atomic Operations)           │
                │  - Counters      - Sorted Sets            │
                │  - Token State   - TTL Windows            │
                └──────────────────────────────────────────┘
```

---

## ⚙️ **Tech Stack**

* **Java 21**
* **Spring Boot 3**
* **Spring Web MVC**
* **Spring Data Redis (Lettuce)**
* **Redis**
* **Lombok**
* **JUnit**
* **Docker (optional)**

---

# 📡 API Endpoints

## **🔹 Check Request Limit**

`POST /check`

### Request:

```json
{
  "apiKey": "user123",
  "permits": 1
}
```

### Response:

```json
{
  "allowed": true,
  "remaining": 42
}
```

---

## **🔹 Create/Update Rule**

`POST /admin/rules`

### Sample Rule:

```json
{
  "apiKey": "user123",
  "limit": 100,
  "windowInSeconds": 60,
  "algorithm": "TOKEN_BUCKET",
  "burstAllowed": true
}
```

---

## **🔹 Get Rule**

`GET /admin/rules/{apiKey}`

---

## **🔹 Metrics**

`GET /admin/metrics/{apiKey}`

---

# 🧠 Algorithms Overview

## **1️⃣ Token Bucket**

* Allows bursts
* Tokens refill over time
* Checking a request is O(1)

## **2️⃣ Sliding Window (Sorted Set or Counter-based)**

* Smooth rate limiting
* Fair distribution
* Good for fintech & payments

---

# 🚀 **Running the Project**

### **1. Clone the repository**

```sh
git clone https://github.com/<your-username>/intelligent-rate-limiter.git
cd intelligent-rate-limiter
```

### **2. Start Redis**

**Option A: Using Docker**

```sh
docker run -p 6379:6379 redis
```

**Option B: Local installation**
Start Redis server normally.

### **3. Run the application**

```sh
mvn spring-boot:run
```

---

# 🧪 Testing

Run all tests:

```sh
mvn test
```

---

# 🧱 Project Structure

```
src/
 ├── main/java/com.harsh.ratelimiter
 │     ├── controller
 │     ├── service
 │     ├── model
 │     ├── repository
 │     └── config
 ├── test/java/com.harsh.ratelimiter
docs/
 └── daily-progress/
        ├── day1.md
        ├── day2.md
        └── ...
```

---

# 📝 Daily Progress Logs

Daily progress is maintained at:
`/docs/daily-progress/dayX.md`

---

# 📈 Roadmap

* [ ] Add Circuit Breaker (Resilience4j)
* [ ] Add Kafka-based rule propagation
* [ ] Add Redis Cluster support
* [ ] Add Multi-tenancy
* [ ] Build UI Admin Dashboard
* [ ] Deploy on AWS ECS + Elasticache

---

# 🤝 Contributing

PRs are welcome!
Fork the repo, create a branch, and submit a pull request.

---

# 📄 License

MIT License.

---

# 💬 Contact

For questions or collaboration:
**Harsh Bhardwaj**
GitHub: *Harsh81181*
