# E-Commerce Microservices Platform - Usage Guide

Complete guide for building, running, testing, and monitoring the E-Commerce Microservices Platform.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Build & Run](#build--run)
- [Service Endpoints](#service-endpoints)
- [API Examples](#api-examples)
- [Swagger/OpenAPI](#swaggeropenapi)
- [Testing](#testing)
- [Frontend Application](#frontend-application)
- [Database Mock Data](#database-mock-data)
- [Monitoring & Observability](#monitoring--observability)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

1. **Docker Desktop** (version 20.10+)
   - Download: https://www.docker.com/products/docker-desktop
   - Ensure Docker is running before starting services

2. **Java 17** (for local development)
   ```bash
   java -version  # Should show Java 17
   ```

3. **Maven 3.9+** (for building services)
   ```bash
   mvn -version
   ```

4. **Git** (for cloning repository)

### System Requirements

- **RAM**: Minimum 8GB, Recommended 16GB
- **CPU**: 4+ cores recommended
- **Disk Space**: 10GB+ free space
- **OS**: Linux, macOS, or Windows 10/11

### Port Requirements

Ensure the following ports are available:

| Ports | Services |
|-------|----------|
| 8080-8087 | Microservices |
| 8761 | Eureka Server |
| 8888 | Config Server |
| 5432-5435 | PostgreSQL databases |
| 6379 | Redis |
| 5672, 15672 | RabbitMQ |
| 9090 | Prometheus |
| 3000 | Grafana |
| 9411 | Zipkin |
| 3100 | Loki |

Check port availability:
```bash
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows
```

---

## Build & Run

### Option 1: Quick Start (Recommended)

```bash
# Clone repository
git clone <repository-url>
cd api-avengers-mock

# Build and start everything
./scripts/quick-start.sh
```

This script will:
1. Build all microservices
2. Start all Docker containers
3. Wait for services to be healthy
4. Display access points

### Option 2: Manual Build

```bash
# Build all services
./scripts/build-all.sh

# Start Docker Compose
docker-compose up -d

# Check service status
docker-compose ps
```

### Option 3: Build Individual Services

```bash
# Build specific service
cd user-service
mvn clean package -DskipTests

# Build with tests
mvn clean package

# Return to root
cd ..
```

### Verify Installation

```bash
# Check Eureka Dashboard (all services should be registered)
open http://localhost:8761

# Check API Gateway health
curl http://localhost:8080/actuator/health

# View logs
docker-compose logs -f
```

### Stopping Services

```bash
# Stop all services
./scripts/stop-all.sh

# Or manually
docker-compose down

# Stop and remove volumes (reset databases)
docker-compose down -v
```

---

## Service Endpoints

### Service Ports Table

| Service | Port | Base Path | Swagger UI | Actuator |
|---------|------|-----------|------------|----------|
| **API Gateway** | 8080 | /api | http://localhost:8080/swagger-ui.html | /actuator |
| **Eureka Server** | 8761 | / | - | /actuator |
| **Config Server** | 8888 | / | - | /actuator |
| **User Service** | 8081 | /api/users | http://localhost:8081/swagger-ui.html | /actuator |
| **Product Service** | 8082 | /api/products | http://localhost:8082/swagger-ui.html | /actuator |
| **Inventory Service** | 8083 | /api/inventory | http://localhost:8083/swagger-ui.html | /actuator |
| **Cart Service** | 8084 | /api/cart | http://localhost:8084/swagger-ui.html | /actuator |
| **Order Service** | 8085 | /api/orders | http://localhost:8085/swagger-ui.html | /actuator |
| **Payment Service** | 8086 | /api/payments | http://localhost:8086/swagger-ui.html | /actuator |
| **Notification Service** | 8087 | /api/notifications | - | /actuator |

### Direct Service Access

Services can be accessed directly (bypassing gateway) for debugging:

```bash
# User Service
curl http://localhost:8081/actuator/health

# Product Service
curl http://localhost:8082/actuator/health
```

### Gateway Routes

All API calls should go through the API Gateway (port 8080):

```
http://localhost:8080/api/users/*      → User Service
http://localhost:8080/api/products/*   → Product Service
http://localhost:8080/api/inventory/*  → Inventory Service
http://localhost:8080/api/cart/*       → Cart Service
http://localhost:8080/api/orders/*     → Order Service
http://localhost:8080/api/payments/*   → Payment Service
```

---

## API Examples

### Complete Order Flow

Run the automated test script:
```bash
./scripts/test-api.sh
```

Or follow these manual steps:

### 1. Register a New User

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "test@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CUSTOMER"
}
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

Save the token:
```bash
export TOKEN="<token-from-response>"
```

### 3. Get User Profile

```bash
curl http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Browse Products

```bash
# Get all products
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN"

# Get specific product
curl http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer $TOKEN"

# Search products
curl "http://localhost:8080/api/products/search?query=Laptop" \
  -H "Authorization: Bearer $TOKEN"

# Get by category
curl http://localhost:8080/api/products/category/Electronics \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Add Items to Cart

```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "productName": "MacBook Pro",
    "price": 2499.99,
    "quantity": 2
  }'
```

### 6. View Cart

```bash
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer $TOKEN"
```

### 7. Update Cart Item Quantity

```bash
curl -X PUT "http://localhost:8080/api/cart/update?productId=1&quantity=3" \
  -H "Authorization: Bearer $TOKEN"
```

### 8. Remove from Cart

```bash
curl -X DELETE http://localhost:8080/api/cart/remove/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 9. Place Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

**Note**: This triggers the full saga pattern:
1. Creates order
2. Reserves inventory
3. Processes payment
4. Confirms order or rollback if payment fails

### 10. View Orders

```bash
# Get all orders
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"

# Get specific order
curl http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer $TOKEN"
```

### 11. Cancel Order

```bash
curl -X POST http://localhost:8080/api/orders/1/cancel \
  -H "Authorization: Bearer $TOKEN"
```

---

## Swagger/OpenAPI

Every service has interactive API documentation via Swagger UI.

### Access Swagger UI

| Service | Swagger URL |
|---------|-------------|
| User Service | http://localhost:8081/swagger-ui.html |
| Product Service | http://localhost:8082/swagger-ui.html |
| Inventory Service | http://localhost:8083/swagger-ui.html |
| Cart Service | http://localhost:8084/swagger-ui.html |
| Order Service | http://localhost:8085/swagger-ui.html |
| Payment Service | http://localhost:8086/swagger-ui.html |
| API Gateway | http://localhost:8080/swagger-ui.html |

### Using Swagger UI

1. **Open Swagger UI** for any service
2. **Authorize**: Click "Authorize" button, enter JWT token: `Bearer <token>`
3. **Try API**: Expand endpoints, click "Try it out", fill parameters, execute
4. **View Response**: See response code, body, and headers

### OpenAPI Specification

Download OpenAPI JSON spec:
```bash
curl http://localhost:8081/v3/api-docs > user-service-api.json
curl http://localhost:8082/v3/api-docs > product-service-api.json
```

---

## Testing

### Unit Tests (Mockito)

All services have comprehensive Mockito unit tests covering:
- Success scenarios
- Failure scenarios
- Edge cases
- Circuit breaker scenarios (Order Service)

Run tests:
```bash
# Test all services
./scripts/test-all.sh

# Test specific service
cd user-service && mvn test
cd product-service && mvn test
cd order-service && mvn test
cd cart-service && mvn test
```

Test coverage includes:
- **UserService**: Registration, login, password validation, user lookup
- **ProductService**: CRUD operations, search, category filtering
- **OrderService**: Order creation, saga pattern, circuit breaker, cancellation
- **CartService**: Add/remove items, quantity updates, cart expiration

### Load Testing (K6)

K6 load tests simulate realistic user traffic.

#### Install K6

```bash
# macOS
brew install k6

# Linux
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6

# Windows (via Chocolatey)
choco install k6
```

#### Run Load Tests

```bash
# Test order flow (ramps up to 50 users)
k6 run k6/load-test-orders.js

# Test product browsing (ramps up to 100 users)
k6 run k6/load-test-products.js

# Test authentication (ramps up to 60 users)
k6 run k6/load-test-auth.js

# Test complete flow (comprehensive test)
k6 run k6/load-test-full-flow.js
```

#### Custom K6 Parameters

```bash
# Run with custom duration and users
k6 run --vus 100 --duration 5m k6/load-test-products.js

# Set custom base URL
BASE_URL=http://staging.example.com k6 run k6/load-test-orders.js
```

#### Understanding K6 Output

```
scenarios: (100.00%) 1 scenario, 50 max VUs, 2m30s max duration
✓ registration successful
✓ products loaded
✓ product added to cart
✓ cart retrieved
✓ order placed

checks.........................: 95.23% ✓ 1523      ✗ 76
data_received..................: 2.3 MB 45 kB/s
data_sent......................: 1.1 MB 22 kB/s
http_req_duration..............: avg=245ms min=12ms med=198ms max=1.2s p(95)=523ms
http_reqs......................: 3245   64.9/s
```

---

## Frontend Application

A simple vanilla JavaScript SPA for interacting with the platform.

### Running the Frontend

```bash
cd frontend

# Option 1: Python HTTP Server
python3 -m http.server 8000

# Option 2: Node.js serve
npx serve -p 8000

# Option 3: PHP
php -S localhost:8000
```

Access: http://localhost:8000

### Features

- **Authentication**: Login and registration
- **Product Browsing**: View all products, search, filter by category
- **Shopping Cart**: Add/remove items, update quantities
- **Order Management**: Place orders, view order history
- **Real-time Updates**: Dynamic cart counter, order status

### API Configuration

Edit `frontend/app.js` to change API base URL:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

---

## Database Mock Data

Pre-populated sample data for testing.

### Load Mock Data

```bash
# Load products (50 diverse products)
docker exec -i postgres-product psql -U postgres -d productdb < database/init/products.sql

# Load inventory (stock for all products)
docker exec -i postgres-inventory psql -U postgres -d inventorydb < database/init/inventory.sql

# Load users (10 sample users)
docker exec -i postgres-user psql -U postgres -d userdb < database/init/users.sql
```

### Sample Data Contents

**Products** (50 items across 5 categories):
- Electronics: Laptops, phones, cameras, monitors
- Clothing: Shoes, jeans, jackets, accessories
- Books: Programming, self-help, fiction
- Home & Kitchen: Appliances, smart home devices
- Sports: Fitness equipment, camping gear

**Users** (10 users, password: `password123`):
- admin@ecommerce.com (ADMIN role)
- john.doe@example.com (CUSTOMER)
- jane.smith@example.com (CUSTOMER)
- ... and 7 more customers

**Inventory**: Stock levels for all products (ranging from 10-250 units)

### Direct Database Access

```bash
# Connect to user database
docker exec -it postgres-user psql -U postgres -d userdb

# Connect to product database
docker exec -it postgres-product psql -U postgres -d productdb

# SQL queries
SELECT * FROM users;
SELECT * FROM products WHERE category = 'Electronics';
SELECT * FROM inventory WHERE available_quantity < 50;
```

---

## Monitoring & Observability

### Zipkin (Distributed Tracing)

**URL**: http://localhost:9411

**Usage**:
1. Open Zipkin UI
2. Click "Run Query" to see recent traces
3. Click on a trace to see the full request path
4. Analyze latency across services
5. Identify bottlenecks

**Key Features**:
- See complete request journey across all services
- Latency breakdown per service
- Dependency graph
- Error tracking

### Prometheus (Metrics)

**URL**: http://localhost:9090

**Usage**:
1. Open Prometheus UI
2. Go to "Graph" tab
3. Enter PromQL queries
4. View metrics graphs

**Sample Queries**:
```promql
# Request rate
rate(http_server_requests_seconds_count[1m])

# Error rate
rate(http_server_requests_seconds_count{status="500"}[1m])

# 95th percentile latency
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# JVM memory usage
jvm_memory_used_bytes

# Active connections
hikaricp_connections_active
```

### Grafana (Dashboards)

**URL**: http://localhost:3000
**Login**: admin / admin

**Usage**:
1. Login with admin/admin
2. Go to "Dashboards"
3. View pre-configured dashboards
4. Create custom dashboards

**Available Dashboards**:
- Spring Boot Statistics
- JVM Metrics
- Database Connection Pools
- HTTP Request Metrics

**Creating Custom Dashboard**:
1. Click "+ Create Dashboard"
2. Add Panel
3. Select Prometheus as data source
4. Write PromQL query
5. Configure visualization

### RabbitMQ (Message Queue)

**URL**: http://localhost:15672
**Login**: admin / admin

**Usage**:
1. Login to management console
2. View "Queues" tab
3. Monitor message rates
4. Check consumer status

**Key Metrics**:
- Message rate (in/out)
- Queue depth
- Consumer count
- Unacknowledged messages

---

## Troubleshooting

### Services Not Starting

**Issue**: Docker containers not starting

**Solutions**:
```bash
# Check logs
docker-compose logs -f <service-name>

# Restart specific service
docker-compose restart <service-name>

# Rebuild and restart
docker-compose up -d --build <service-name>

# Check resource usage
docker stats
```

### Port Conflicts

**Issue**: Port already in use

**Solutions**:
```bash
# Find process using port (macOS/Linux)
lsof -i :8080

# Find process using port (Windows)
netstat -ano | findstr :8080

# Kill process
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows

# Or change ports in docker-compose.yml
```

### Database Connection Issues

**Issue**: Cannot connect to database

**Solutions**:
```bash
# Check database containers
docker-compose ps | grep postgres

# Restart databases
docker-compose restart postgres-user postgres-product postgres-inventory postgres-order

# Reset databases (WARNING: deletes data)
docker-compose down -v
docker-compose up -d
```

### Service Not Registered in Eureka

**Issue**: Service not appearing in Eureka dashboard

**Solutions**:
1. Wait 30-60 seconds (registration takes time)
2. Check service logs: `docker-compose logs -f <service-name>`
3. Verify Eureka URL in service config
4. Restart service: `docker-compose restart <service-name>`

### JWT Authentication Failures

**Issue**: 401 Unauthorized errors

**Solutions**:
1. Verify token format: `Authorization: Bearer <token>`
2. Check token expiration
3. Re-login to get fresh token
4. Verify secret key matches across gateway and user service

### Circuit Breaker Activated

**Issue**: Order service circuit breaker open

**Solutions**:
1. Check payment service health: `curl http://localhost:8086/actuator/health`
2. Wait for circuit to half-open (30 seconds)
3. Check Grafana for error rates
4. Review order service logs

### High Memory Usage

**Issue**: Docker consuming too much memory

**Solutions**:
```bash
# Check memory usage
docker stats

# Restart Docker Desktop

# Allocate more memory to Docker (Docker Desktop settings)

# Reduce JVM heap in docker-compose.yml:
JAVA_OPTS: -Xmx512m -Xms256m
```

### Reset Everything

**Nuclear option** - start completely fresh:
```bash
# Stop all services
docker-compose down -v

# Remove all Docker resources
docker system prune -a -f

# Remove local Maven builds
rm -rf */target

# Rebuild everything
./scripts/build-all.sh
docker-compose up -d --build
```

### Debugging Tips

1. **Check Eureka**: http://localhost:8761 - all services should be registered
2. **Check Logs**: `docker-compose logs -f` - watch for errors
3. **Check Health**: `/actuator/health` endpoints - verify service status
4. **Check Zipkin**: http://localhost:9411 - trace request failures
5. **Check RabbitMQ**: http://localhost:15672 - verify message flow

---

## Additional Resources

- **Main README**: [README.md](README.md) - Overview and quick start
- **Database Init**: [database/init/README.md](database/init/README.md) - Mock data details
- **Scripts**: [scripts/](scripts/) - Automation scripts
- **K6 Tests**: [k6/](k6/) - Load testing scripts
- **Frontend**: [frontend/](frontend/) - Web application

---

**For more help, check service logs or open an issue on GitHub.**
