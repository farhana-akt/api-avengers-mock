# ⚡ Quick Start Guide

Get the E-Commerce Microservices Platform running in 5 minutes.

## Prerequisites

- ✅ Docker Desktop installed and running
- ✅ 8GB+ RAM available
- ✅ Ports 8080-8090, 5432-5435, 6379, 5672, 9090, 3000, 9411 available

## 🚀 One-Command Start

```bash
./quick-start.sh
```

This script will:
1. Build all microservices
2. Start all Docker containers
3. Wait for services to be healthy
4. Show you access URLs

**That's it!** ✨

---

## 📝 Manual Start (Step-by-Step)

### Step 1: Build All Services

```bash
./build-all.sh
```

This compiles all Spring Boot microservices with Maven.

### Step 2: Start Docker Containers

```bash
docker-compose up -d
```

This starts:
- 10 microservices
- 4 PostgreSQL databases
- 1 Redis instance
- 1 RabbitMQ instance
- Zipkin, Prometheus, Grafana, Loki

### Step 3: Wait for Services (2-3 minutes)

```bash
# Watch logs
docker-compose logs -f

# Check service status
docker-compose ps
```

### Step 4: Verify Services

```bash
# Check Eureka - All services should be registered
open http://localhost:8761

# Check API Gateway health
curl http://localhost:8080/actuator/health
```

---

## 🧪 Test the System

### Option 1: Automated Test

```bash
./test-api.sh
```

This script will:
- Register a new user
- Login and get JWT token
- Browse products
- Add items to cart
- Place an order
- View order history

### Option 2: Manual Test

**1. Register a User**
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**2. Login and Get Token**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'

# Save the token
export JWT="<your-token-here>"
```

**3. Browse Products**
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $JWT"
```

**4. Add to Cart**
```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

**5. Place Order**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $JWT"
```

---

## 📊 Access Dashboards

Once services are running:

| Dashboard | URL | Credentials |
|-----------|-----|-------------|
| **Eureka** | http://localhost:8761 | - |
| **Zipkin** | http://localhost:9411 | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **RabbitMQ** | http://localhost:15672 | admin/admin |

---

## 🎯 What to Explore

### 1. Service Discovery (Eureka)
- http://localhost:8761
- See all 10 registered microservices
- Real-time health status

### 2. Distributed Tracing (Zipkin)
- http://localhost:9411
- Place an order
- Click "Run Query"
- See the complete request flow across services
- Identify bottlenecks

### 3. Metrics (Prometheus + Grafana)
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Login to Grafana (admin/admin)
- Add Prometheus data source: `http://prometheus:9090`
- Create dashboards for:
  - Request rate
  - Error rate
  - Latency (p95, p99)
  - Circuit breaker states

### 4. Message Queue (RabbitMQ)
- http://localhost:15672 (admin/admin)
- Place an order
- See messages in queues:
  - `order-events-queue`
  - `payment-events-queue`
- Watch notification service consume messages

### 5. Circuit Breaker in Action
```bash
# Stop payment service
docker-compose stop payment-service

# Try placing an order
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $JWT"

# You'll get: Order status PAYMENT_PENDING
# Circuit breaker prevented cascade failure!

# Check circuit breaker metrics
curl http://localhost:8085/actuator/circuitbreakers
```

---

## 🛑 Stop Everything

```bash
./stop-all.sh
```

Or:

```bash
docker-compose down
```

To also remove databases:

```bash
docker-compose down -v
```

---

## 🔧 Troubleshooting

### Services Not Starting

**Check logs:**
```bash
docker-compose logs -f <service-name>
```

**Check Docker resources:**
- Docker Desktop → Preferences → Resources
- Ensure at least 8GB RAM allocated

### Port Conflicts

**Find what's using a port:**
```bash
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows
```

**Kill the process or change port in docker-compose.yml**

### Services Not Registered in Eureka

**Wait 30-60 seconds** - Registration takes time

**Check service logs:**
```bash
docker-compose logs -f user-service
```

**Restart Eureka:**
```bash
docker-compose restart eureka-server
```

### Complete Reset

```bash
docker-compose down -v
docker system prune -a
./quick-start.sh
```

For more issues, see [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

---

## 📚 Learn More

- **Architecture**: See [IMPLEMENTATION.md](IMPLEMENTATION.md) for detailed architecture
- **Ports**: See [PORTS.md](PORTS.md) for complete port reference
- **API Docs**: See [IMPLEMENTATION.md](IMPLEMENTATION.md) API section
- **Troubleshooting**: See [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

---

## 🎓 Key Concepts Demonstrated

1. **Microservices Architecture**
   - 10 independent services
   - Database per service
   - Loose coupling

2. **API Gateway Pattern**
   - Single entry point
   - JWT validation ONCE at gateway
   - Services trust gateway headers

3. **Service Discovery**
   - Eureka for dynamic registration
   - No hardcoded URLs
   - Load balancing

4. **Resilience Patterns**
   - Circuit breaker (Resilience4j)
   - Compensating transactions
   - Fallback methods

5. **Event-Driven Architecture**
   - RabbitMQ for async communication
   - Order and payment events
   - Notification service consumes events

6. **Observability**
   - Zipkin for distributed tracing
   - Prometheus for metrics
   - Grafana for visualization
   - Loki for logs

---

## 🚀 Next Steps

1. ✅ Place multiple orders
2. ✅ View traces in Zipkin
3. ✅ Monitor metrics in Grafana
4. ✅ Test circuit breaker by stopping services
5. ✅ Watch RabbitMQ message flow
6. ✅ Check database content:
   ```bash
   docker exec -it postgres-user psql -U postgres -d userdb
   \dt
   SELECT * FROM users;
   ```

---

**🎉 Congratulations! You're running a production-grade microservices platform!**

For hackathon: Focus on understanding the order flow, circuit breaker, and observability stack.
