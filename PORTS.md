# 🔌 Port Reference Guide

Complete reference for all ports used in the microservices platform.

## 📦 Microservices

| Service | Port | URL | Purpose |
|---------|------|-----|---------|
| **API Gateway** | 8080 | http://localhost:8080 | Single entry point for all API requests |
| **Eureka Server** | 8761 | http://localhost:8761 | Service discovery dashboard |
| **Config Server** | 8888 | http://localhost:8888 | Centralized configuration |
| **User Service** | 8081 | http://localhost:8081 | Authentication & user management |
| **Product Service** | 8082 | http://localhost:8082 | Product catalog |
| **Inventory Service** | 8083 | http://localhost:8083 | Stock management |
| **Cart Service** | 8084 | http://localhost:8084 | Shopping cart |
| **Order Service** | 8085 | http://localhost:8085 | Order processing |
| **Payment Service** | 8086 | http://localhost:8086 | Payment gateway |
| **Notification Service** | 8087 | http://localhost:8087 | Notifications |

## 💾 Databases

| Database | Port | Credentials | Service |
|----------|------|-------------|---------|
| **PostgreSQL (User)** | 5432 | postgres/postgres | User Service |
| **PostgreSQL (Product)** | 5433 | postgres/postgres | Product Service |
| **PostgreSQL (Order)** | 5434 | postgres/postgres | Order Service |
| **PostgreSQL (Inventory)** | 5435 | postgres/postgres | Inventory Service |
| **Redis** | 6379 | - | Cart Service |

**Connection Examples:**
```bash
# User DB
psql -h localhost -p 5432 -U postgres -d userdb

# Product DB
psql -h localhost -p 5433 -U postgres -d productdb

# Order DB
psql -h localhost -p 5434 -U postgres -d orderdb

# Inventory DB
psql -h localhost -p 5435 -U postgres -d inventorydb

# Redis
redis-cli -h localhost -p 6379
```

## 📨 Message Queue

| Service | Port | URL | Credentials |
|---------|------|-----|-------------|
| **RabbitMQ (AMQP)** | 5672 | amqp://localhost:5672 | admin/admin |
| **RabbitMQ Management** | 15672 | http://localhost:15672 | admin/admin |

**RabbitMQ Queues:**
- `order-events-queue` - Order placed events
- `payment-events-queue` - Payment success/failure events

## 📊 Observability

| Tool | Port | URL | Credentials | Purpose |
|------|------|-----|-------------|---------|
| **Zipkin** | 9411 | http://localhost:9411 | - | Distributed tracing |
| **Prometheus** | 9090 | http://localhost:9090 | - | Metrics collection |
| **Grafana** | 3000 | http://localhost:3000 | admin/admin | Dashboards |
| **Loki** | 3100 | http://localhost:3100 | - | Log aggregation |

## 🏥 Health Check Endpoints

All services expose health checks at:
```
http://localhost:<port>/actuator/health
```

Examples:
- API Gateway: http://localhost:8080/actuator/health
- User Service: http://localhost:8081/actuator/health
- Product Service: http://localhost:8082/actuator/health

## 📈 Metrics Endpoints

All services expose Prometheus metrics at:
```
http://localhost:<port>/actuator/prometheus
```

Examples:
- Order Service: http://localhost:8085/actuator/prometheus
- Payment Service: http://localhost:8086/actuator/prometheus

## 🔍 API Routes (via API Gateway)

All requests go through API Gateway (port 8080):

| Route | Service | Example |
|-------|---------|---------|
| `/api/users/**` | User Service | POST /api/users/register |
| `/api/products/**` | Product Service | GET /api/products |
| `/api/inventory/**` | Inventory Service | GET /api/inventory/1 |
| `/api/cart/**` | Cart Service | POST /api/cart/add |
| `/api/orders/**` | Order Service | POST /api/orders |
| `/api/payments/**` | Payment Service | POST /api/payments/process |

## 🔒 Port Requirements

**Required Ports (Must be Available):**
- 8080-8087 (Microservices)
- 5432-5435 (PostgreSQL instances)
- 6379 (Redis)
- 5672, 15672 (RabbitMQ)
- 8761 (Eureka)
- 8888 (Config Server)
- 9090 (Prometheus)
- 9411 (Zipkin)
- 3000 (Grafana)
- 3100 (Loki)

**Check if ports are in use:**
```bash
# macOS/Linux
lsof -i :8080
lsof -i :5432

# Windows
netstat -ano | findstr :8080
```

## 🐳 Docker Network

All services communicate on the `microservices-network` Docker network.

**Internal hostnames (within Docker):**
- `eureka-server`
- `config-server`
- `api-gateway`
- `user-service`
- `product-service`
- `inventory-service`
- `cart-service`
- `order-service`
- `payment-service`
- `notification-service`
- `postgres-user`
- `postgres-product`
- `postgres-order`
- `postgres-inventory`
- `redis`
- `rabbitmq`
- `zipkin`
- `prometheus`
- `grafana`
- `loki`

## 🚦 Service Dependencies

**Startup Order:**
1. **Infrastructure** (PostgreSQL, Redis, RabbitMQ, Zipkin, Prometheus, Grafana, Loki)
2. **Eureka Server** (8761)
3. **Config Server** (8888) - depends on Eureka
4. **API Gateway** (8080) - depends on Eureka & Config
5. **All Microservices** (8081-8087) - depend on Eureka & their databases

**Health Check Wait Times:**
- Infrastructure services: 10s interval
- Eureka Server: 30s start period
- Config Server: 30s start period
- Other services: 60s start period

## 🧪 Testing Ports Locally

```bash
# Test API Gateway
curl http://localhost:8080/actuator/health

# Test Eureka
curl http://localhost:8761/actuator/health

# Test User Service
curl http://localhost:8081/actuator/health

# Test Product Service
curl http://localhost:8082/actuator/health

# Test RabbitMQ
curl http://localhost:15672 (opens browser)

# Test Zipkin
curl http://localhost:9411/health
```

## 📝 Notes

- **External Access**: Use `localhost` and external ports (5432, 5433, etc.)
- **Internal Docker**: Services use container names and internal ports
- **API Gateway**: All external API requests go through port 8080
- **Direct Service Access**: Services can be accessed directly for debugging (ports 8081-8087)
- **Database Access**: Each service's database is exposed on unique external port

## 🔧 Troubleshooting Port Conflicts

If you encounter port conflicts:

1. **Identify the process:**
   ```bash
   lsof -i :8080  # or whichever port
   ```

2. **Stop the conflicting process:**
   ```bash
   kill -9 <PID>
   ```

3. **Or change port in docker-compose.yml:**
   ```yaml
   ports:
     - "8090:8080"  # External:Internal
   ```

4. **Restart services:**
   ```bash
   docker-compose down
   docker-compose up -d
   ```
