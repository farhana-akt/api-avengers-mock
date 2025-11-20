# E-Commerce Microservices Platform - Implementation Guide

## 🎯 Overview

This is a complete microservices-based e-commerce platform demonstrating Spring Boot microservices architecture with industry-standard DevOps practices.

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Services & Ports](#services--ports)
- [Technology Stack](#technology-stack)
- [Quick Start](#quick-start)
- [Service Details](#service-details)
- [Observability Stack](#observability-stack)
- [Security Architecture](#security-architecture)
- [Testing Strategy](#testing-strategy)
- [API Documentation](#api-documentation)

---

## 🏗️ Architecture Overview

### **Microservices Pattern**
- **Database Per Service**: Each microservice has its own PostgreSQL database
- **Service Discovery**: Eureka Server for dynamic service registration
- **Centralized Configuration**: Config Server with Git-backed configuration
- **API Gateway Pattern**: Single entry point with JWT validation
- **Event-Driven Architecture**: RabbitMQ for asynchronous communication
- **Circuit Breaker**: Resilience4j for fault tolerance

### **JWT Authentication Pattern**
✅ **Gateway validates JWT ONCE** → Services trust headers (Best practice)
- API Gateway validates JWT signature
- Extracts user context (userId, role, email)
- Passes context in headers to downstream services
- Services read `X-User-Id` header (no JWT validation needed)

---

## 🔌 Services & Ports

### **Infrastructure Services**

| Service | Port | Purpose | UI/Endpoint |
|---------|------|---------|-------------|
| **Eureka Server** | 8761 | Service Registry | http://localhost:8761 |
| **Config Server** | 8888 | Centralized Config | http://localhost:8888 |
| **API Gateway** | 8080 | Entry Point | http://localhost:8080 |

### **Business Microservices**

| Service | Port | Database | Purpose |
|---------|------|----------|---------|
| **User Service** | 8081 | PostgreSQL (5432) | Authentication, User Management, JWT Generation |
| **Product Service** | 8082 | PostgreSQL (5433) | Product Catalog, Search, Categories |
| **Inventory Service** | 8083 | PostgreSQL (5435) | Stock Management, Availability |
| **Cart Service** | 8084 | Redis (6379) | Shopping Cart, Session Management |
| **Order Service** | 8085 | PostgreSQL (5434) | Order Processing, Orchestration |
| **Payment Service** | 8086 | - | Mock Payment Gateway |
| **Notification Service** | 8087 | - | Email/SMS Notifications |

### **Data Stores**

| Service | Port | Credentials | Purpose |
|---------|------|-------------|---------|
| **PostgreSQL (User)** | 5432 | postgres/postgres | User data |
| **PostgreSQL (Product)** | 5433 | postgres/postgres | Product data |
| **PostgreSQL (Order)** | 5434 | postgres/postgres | Order data |
| **PostgreSQL (Inventory)** | 5435 | postgres/postgres | Inventory data |
| **Redis** | 6379 | - | Cart cache |
| **RabbitMQ** | 5672 (AMQP) | admin/admin | Message queue |
| **RabbitMQ UI** | 15672 | admin/admin | Management console |

### **Observability Stack**

| Tool | Port | Credentials | Purpose |
|------|------|-------------|---------|
| **Zipkin** | 9411 | - | Distributed Tracing |
| **Prometheus** | 9090 | - | Metrics Collection |
| **Grafana** | 3000 | admin/admin | Dashboards & Visualization |
| **Loki** | 3100 | - | Log Aggregation |

---

## 🛠️ Technology Stack

### **Core Framework**
- **Spring Boot 3.2.0** - Application framework
- **Spring Cloud 2023.0.0** - Microservices tools
- **Java 17** - Programming language
- **Maven** - Build tool

### **Spring Cloud Components**
- **Spring Cloud Netflix Eureka** - Service discovery
- **Spring Cloud Config** - Centralized configuration
- **Spring Cloud Gateway** - API gateway with routing
- **Spring Cloud OpenFeign** - Declarative REST clients
- **Spring Cloud Circuit Breaker** - Fault tolerance

### **Data & Caching**
- **Spring Data JPA** - ORM layer
- **PostgreSQL 14** - Relational database
- **Redis 7** - In-memory cache
- **Flyway** - Database migration

### **Messaging**
- **RabbitMQ 3.12** - Message broker
- **Spring AMQP** - RabbitMQ integration

### **Resilience**
- **Resilience4j** - Circuit breaker, retry, rate limiter
- **Spring Retry** - Retry logic

### **Observability**
- **Micrometer Tracing** - Distributed tracing abstraction
- **Zipkin** - Tracing backend
- **Prometheus** - Metrics collection
- **Grafana** - Metrics visualization
- **Loki** - Log aggregation

### **Security**
- **Spring Security** - Security framework
- **JWT (jjwt)** - Token-based authentication
- **BCrypt** - Password hashing

### **Testing**
- **JUnit 5** - Unit testing
- **Mockito** - Mocking framework
- **Testcontainers** - Integration testing
- **REST Assured** - API testing

### **Containerization**
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

---

## 🚀 Quick Start

### **Prerequisites**
- Docker Desktop installed
- 8GB+ RAM available
- Ports 8080-8090, 5432-5435, 6379, 5672, 9090, 3000, 9411 available

### **Step 1: Clone and Build**
```bash
git clone <repository-url>
cd api-avengers-mock
```

### **Step 2: Build All Services**
```bash
# Build each microservice
cd eureka-server && mvn clean package -DskipTests && cd ..
cd config-server && mvn clean package -DskipTests && cd ..
cd api-gateway && mvn clean package -DskipTests && cd ..
cd user-service && mvn clean package -DskipTests && cd ..
cd product-service && mvn clean package -DskipTests && cd ..
cd inventory-service && mvn clean package -DskipTests && cd ..
cd cart-service && mvn clean package -DskipTests && cd ..
cd order-service && mvn clean package -DskipTests && cd ..
cd payment-service && mvn clean package -DskipTests && cd ..
cd notification-service && mvn clean package -DskipTests && cd ..
```

### **Step 3: Start Everything**
```bash
docker-compose up -d
```

### **Step 4: Verify Services**
Wait 2-3 minutes for all services to start, then check:

```bash
# Check Eureka Dashboard - All services should be registered
open http://localhost:8761

# Check API Gateway health
curl http://localhost:8080/actuator/health

# Check Grafana
open http://localhost:3000  # Login: admin/admin

# Check RabbitMQ Management
open http://localhost:15672  # Login: admin/admin

# Check Zipkin
open http://localhost:9411
```

### **Step 5: Test the System**

**1. Register a User**
```bash
curl -X POST http://localhost:8080/api/v1/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**2. Login and Get JWT Token**
```bash
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'

# Response: {"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}
```

**3. Browse Products (with JWT)**
```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**4. Add to Cart**
```bash
curl -X POST http://localhost:8080/api/v1/cart/items \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

**5. Place Order**
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

---

## 📦 Service Details

### **1. Eureka Server (Port 8761)**

**Purpose**: Service registry and discovery

**Key Features**:
- Auto-registration of microservices
- Health monitoring with heartbeats
- Service instance lookup
- Load balancing support

**Access**:
- Dashboard: http://localhost:8761
- All registered services visible in UI

**Configuration**:
```yaml
eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
```

---

### **2. Config Server (Port 8888)**

**Purpose**: Centralized configuration management

**Key Features**:
- Git-backed configuration
- Environment-specific profiles (dev, prod)
- Dynamic configuration refresh
- Encrypted properties support

**Endpoints**:
- `GET /{application}/{profile}` - Get configuration
- `GET /{application}-{profile}.yml` - Get YAML config

**Example**:
```bash
curl http://localhost:8888/user-service/docker
```

---

### **3. API Gateway (Port 8080)**

**Purpose**: Single entry point with JWT validation

**Key Responsibilities**:
1. **JWT Validation** (ONCE at gateway level)
2. **Routing** to microservices
3. **Load Balancing** across instances
4. **Rate Limiting**
5. **CORS handling**

**JWT Validation Flow**:
```
1. Client sends: Authorization: Bearer <JWT>
2. Gateway validates JWT signature
3. Gateway extracts: userId, role, email
4. Gateway adds headers:
   - X-User-Id: 123
   - X-User-Role: CUSTOMER
   - X-User-Email: user@example.com
5. Downstream services trust these headers
```

**Routes**:
- `/api/v1/users/**` → User Service
- `/api/v1/products/**` → Product Service
- `/api/v1/cart/**` → Cart Service
- `/api/v1/orders/**` → Order Service

---

### **4. User Service (Port 8081)**

**Purpose**: Authentication and user management

**Database**: PostgreSQL (port 5432) - `userdb`

**Key Features**:
- User registration with BCrypt password hashing
- JWT token generation (HS256 algorithm)
- User profile management
- Role-based access (ADMIN, CUSTOMER)

**Endpoints**:
- `POST /api/v1/users/register` - Register new user
- `POST /api/v1/users/login` - Login and get JWT
- `GET /api/v1/users/profile` - Get user profile (requires JWT)
- `PUT /api/v1/users/profile` - Update profile

**JWT Token Structure**:
```json
{
  "sub": "user@example.com",
  "userId": 123,
  "role": "CUSTOMER",
  "iat": 1700000000,
  "exp": 1700003600
}
```

**Database Schema**:
```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  role VARCHAR(20) DEFAULT 'CUSTOMER',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### **5. Product Service (Port 8082)**

**Purpose**: Product catalog management

**Database**: PostgreSQL (port 5433) - `productdb`

**Key Features**:
- Product CRUD operations
- Category management
- Search and filtering
- Pagination support

**Endpoints**:
- `GET /api/v1/products` - List all products (paginated)
- `GET /api/v1/products/{id}` - Get product by ID
- `POST /api/v1/products` - Create product (ADMIN only)
- `PUT /api/v1/products/{id}` - Update product
- `DELETE /api/v1/products/{id}` - Delete product
- `GET /api/v1/products/search?name={name}` - Search products

**Database Schema**:
```sql
CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  category VARCHAR(100),
  image_url VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### **6. Inventory Service (Port 8083)**

**Purpose**: Stock management

**Database**: PostgreSQL (port 5435) - `inventorydb`

**Key Features**:
- Stock availability checking
- Stock reservation (with transactions)
- Stock release (compensating transaction)
- Low stock alerts

**Endpoints**:
- `GET /api/v1/inventory/{productId}` - Check stock
- `POST /api/v1/inventory/reserve` - Reserve stock
- `POST /api/v1/inventory/release` - Release reserved stock
- `PUT /api/v1/inventory/{productId}` - Update stock level

**Transaction Handling**:
```java
@Transactional
public void reserveStock(Long productId, Integer quantity) {
    // SELECT FOR UPDATE prevents race conditions
    Inventory inv = inventoryRepository.findByProductIdForUpdate(productId);
    if (inv.getStock() < quantity) {
        throw new InsufficientStockException();
    }
    inv.setStock(inv.getStock() - quantity);
    inventoryRepository.save(inv);
}
```

---

### **7. Cart Service (Port 8084)**

**Purpose**: Shopping cart management

**Data Store**: Redis (port 6379)

**Key Features**:
- Add/remove items from cart
- Update quantities
- Calculate totals
- Session-based carts (stored in Redis)

**Why Redis?**
- Fast in-memory storage
- TTL support (cart expires after 24 hours)
- Session management
- No need for persistent storage

**Endpoints**:
- `GET /api/v1/cart` - Get user's cart
- `POST /api/v1/cart/items` - Add item to cart
- `PUT /api/v1/cart/items/{productId}` - Update quantity
- `DELETE /api/v1/cart/items/{productId}` - Remove item
- `DELETE /api/v1/cart` - Clear cart

**Redis Data Structure**:
```
Key: cart:userId:123
Value: {
  "items": [
    {"productId": 1, "quantity": 2, "price": 500},
    {"productId": 5, "quantity": 1, "price": 300}
  ],
  "total": 1300
}
TTL: 86400 seconds (24 hours)
```

---

### **8. Order Service (Port 8085)**

**Purpose**: Order orchestration (central coordinator)

**Database**: PostgreSQL (port 5434) - `orderdb`

**Key Features**:
- Place orders (orchestrates multiple services)
- Order history
- Order status tracking
- Circuit breaker for external calls

**Dependencies** (via OpenFeign):
- Cart Service - Get cart items
- Product Service - Validate products
- Inventory Service - Reserve stock (with Circuit Breaker)
- Payment Service - Process payment (with Circuit Breaker)

**Order Placement Flow**:
```
1. Get cart items (CartService)
2. Validate products exist (ProductService)
3. Calculate total
4. Reserve stock (InventoryService + Circuit Breaker)
   → If fails: Return error
5. Process payment (PaymentService + Circuit Breaker)
   → If fails: Release stock (compensating transaction)
6. Save order to database
7. Publish OrderPlaced event to RabbitMQ
8. Clear cart
9. Return order confirmation
```

**Endpoints**:
- `POST /api/v1/orders` - Place order
- `GET /api/v1/orders` - Get user's orders
- `GET /api/v1/orders/{id}` - Get order details
- `PUT /api/v1/orders/{id}/cancel` - Cancel order

**Circuit Breaker Configuration**:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 60s
        permittedNumberOfCallsInHalfOpenState: 3
```

---

### **9. Payment Service (Port 8086)**

**Purpose**: Mock payment processing

**Key Features**:
- Mock payment gateway (90% success rate for demo)
- Payment status tracking
- Publishes payment events to RabbitMQ

**Endpoints**:
- `POST /api/v1/payments/process` - Process payment

**Mock Implementation**:
```java
public PaymentResponse processPayment(PaymentRequest request) {
    // 90% success rate for demo
    if (Math.random() < 0.9) {
        String txnId = "TXN-" + UUID.randomUUID();
        publishPaymentSuccessEvent(request.getOrderId(), txnId);
        return new PaymentResponse("SUCCESS", txnId);
    }
    throw new PaymentFailedException("Payment declined");
}
```

**RabbitMQ Events**:
- Publishes: `PaymentSuccessEvent`, `PaymentFailedEvent`

---

### **10. Notification Service (Port 8087)**

**Purpose**: Asynchronous notifications

**Key Features**:
- Consumes events from RabbitMQ
- Sends email notifications (mock for demo)
- Logs all notifications

**RabbitMQ Consumers**:
- `OrderPlacedEvent` → Send order confirmation email
- `PaymentSuccessEvent` → Send payment receipt
- `OrderShippedEvent` → Send shipping notification

**Implementation**:
```java
@RabbitListener(queues = "order-events-queue")
public void handleOrderPlaced(OrderPlacedEvent event) {
    String email = getUserEmail(event.getUserId());
    sendEmail(email, "Order Confirmation",
              "Your order #" + event.getOrderId() + " is confirmed!");
}
```

---

## 📊 Observability Stack

### **1. Zipkin (Port 9411) - Distributed Tracing**

**Purpose**: Track requests across microservices

**Access**: http://localhost:9411

**What You See**:
- End-to-end request flow
- Service dependency graph
- Latency breakdown per service
- Error traces

**Example Trace**:
```
TraceId: abc123
Gateway (50ms)
  → Order Service (450ms)
    → Cart Service (30ms)
    → Product Service (40ms)
    → Inventory Service (150ms)
    → Payment Service (180ms)

Total: 500ms
```

**How to Use**:
1. Open http://localhost:9411
2. Click "Run Query"
3. Select a trace to see waterfall diagram
4. Identify slow services (bottlenecks)

---

### **2. Prometheus (Port 9090) - Metrics Collection**

**Purpose**: Collect and store time-series metrics

**Access**: http://localhost:9090

**Metrics Collected**:
- **HTTP Metrics**: Request rate, error rate, latency
- **JVM Metrics**: Heap usage, GC time, thread count
- **Circuit Breaker**: State (open/closed/half-open), failure rate
- **Database**: Connection pool usage, query time

**Key Queries**:

**Request Rate (requests per second)**:
```promql
rate(http_server_requests_seconds_count[1m])
```

**Error Rate**:
```promql
rate(http_server_requests_seconds_count{status=~"5.."}[1m]) /
rate(http_server_requests_seconds_count[1m]) * 100
```

**95th Percentile Latency**:
```promql
histogram_quantile(0.95, http_server_requests_seconds_bucket)
```

**Circuit Breaker State**:
```promql
resilience4j_circuitbreaker_state
```

---

### **3. Grafana (Port 3000) - Dashboards**

**Purpose**: Visualize metrics from Prometheus and Loki

**Access**: http://localhost:3000
**Login**: admin / admin

**Pre-configured Dashboards**:

**1. Microservices Overview Dashboard**:
- Request rate per service
- Error rate per service
- Latency (p50, p95, p99)
- Circuit breaker states
- JVM heap usage

**2. Order Service Dashboard**:
- Orders placed per minute
- Order success rate
- Average order processing time
- Payment failures
- Stock reservation failures

**3. Infrastructure Dashboard**:
- Database connection pool usage
- Redis cache hit rate
- RabbitMQ queue depth
- Service health status

**How to Add Data Sources**:
1. Configuration → Data Sources → Add data source
2. Add Prometheus: http://prometheus:9090
3. Add Loki: http://loki:3100
4. Save & Test

---

### **4. Loki (Port 3100) - Log Aggregation**

**Purpose**: Centralized log collection and querying

**Integration**: Works with Grafana

**Log Query Examples**:

**View Order Service errors**:
```logql
{container="order-service"} |= "ERROR"
```

**Payment failures in last hour**:
```logql
{container="payment-service"} |= "PaymentFailedException" | json | __timestamp__ > 1h
```

**Trace specific request**:
```logql
{container=~".*-service"} |= "traceId=abc123"
```

---

## 🔒 Security Architecture

### **JWT Token Flow**

**1. User Login**:
```
User → API Gateway → User Service
User Service validates credentials
User Service generates JWT token
JWT contains: userId, email, role, expiration
Return JWT to user
```

**2. Authenticated Request**:
```
User sends: Authorization: Bearer <JWT>

API Gateway:
  1. Extracts JWT from Authorization header
  2. Validates signature using secret key
  3. Checks expiration
  4. Extracts claims: userId, role, email
  5. Adds headers:
     - X-User-Id: 123
     - X-User-Role: CUSTOMER
     - X-User-Email: user@example.com
  6. Removes Authorization header
  7. Routes to microservice

Microservice (e.g., Order Service):
  1. Reads X-User-Id header (NO JWT validation)
  2. Trusts gateway (internal network)
  3. Uses userId for business logic
```

### **Why This Pattern is Best**

✅ **Security**: JWT validated once at edge (gateway)
✅ **Performance**: Services don't parse/validate JWT (faster)
✅ **Scalability**: Services are lightweight (no JWT library)
✅ **Simplicity**: Services just read headers (simple code)
✅ **Trust**: Internal network is secured (no external access)

### **Alternative Patterns (NOT Used)**

❌ **Each service validates JWT independently**:
- More secure but slower (repeated JWT parsing)
- Every service needs JWT library + public key
- Higher CPU usage

❌ **Services trust all requests (no validation)**:
- Insecure (vulnerable to insider threats)
- No perimeter security

❌ **Call Auth Service for every request**:
- Very slow (network latency)
- Auth Service becomes bottleneck
- Poor scalability

---

## 🧪 Testing Strategy

### **Unit Tests**

**Location**: `src/test/java` in each service

**Framework**: JUnit 5 + Mockito

**Example: Order Service Test**:
```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartClient cartClient;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrder_Success() {
        // Arrange
        when(cartClient.getCart(anyString()))
            .thenReturn(createMockCart());
        when(paymentClient.processPayment(any()))
            .thenReturn(new PaymentResponse("SUCCESS", "TXN123"));

        // Act
        OrderResponse response = orderService.placeOrder("user123");

        // Assert
        assertEquals("CONFIRMED", response.getStatus());
        verify(cartClient).clearCart("user123");
    }

    @Test
    void placeOrder_PaymentFails_StockReleased() {
        // Test compensating transaction
        when(paymentClient.processPayment(any()))
            .thenThrow(new PaymentException());

        assertThrows(OrderException.class,
            () -> orderService.placeOrder("user123"));

        verify(inventoryClient).releaseStock(any());
    }
}
```

**Run Tests**:
```bash
cd order-service
mvn test
```

---

### **Integration Tests**

**Framework**: Testcontainers (real PostgreSQL/Redis in Docker)

**Example: Product Repository Test**:
```java
@DataJpaTest
@Testcontainers
class ProductRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:14-alpine");

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByCategory_ReturnsProducts() {
        // Arrange
        Product product = new Product();
        product.setName("Laptop");
        product.setCategory("Electronics");
        productRepository.save(product);

        // Act
        List<Product> products =
            productRepository.findByCategory("Electronics");

        // Assert
        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }
}
```

---

### **Contract Tests**

**Purpose**: Ensure service contracts don't break

**Tool**: Spring Cloud Contract

**Example**: Verify Product Service returns correct response format

---

### **End-to-End Tests**

**Tool**: REST Assured

**Example**:
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrderE2ETest {

    @LocalServerPort
    private int port;

    @Test
    void completeOrderFlow() {
        // 1. Login and get JWT
        String jwt = given()
            .contentType("application/json")
            .body(new LoginRequest("user@test.com", "password"))
            .when()
            .post("/api/v1/users/login")
            .then()
            .statusCode(200)
            .extract().path("token");

        // 2. Add to cart
        given()
            .header("Authorization", "Bearer " + jwt)
            .contentType("application/json")
            .body(new CartItem(1L, 2))
            .when()
            .post("/api/v1/cart/items")
            .then()
            .statusCode(200);

        // 3. Place order
        given()
            .header("Authorization", "Bearer " + jwt)
            .when()
            .post("/api/v1/orders")
            .then()
            .statusCode(201)
            .body("status", equalTo("CONFIRMED"));
    }
}
```

---

## 📚 API Documentation

### **Common Headers**

All authenticated endpoints require:
```
Authorization: Bearer <JWT_TOKEN>
```

API Gateway automatically adds:
```
X-User-Id: 123
X-User-Role: CUSTOMER
X-User-Email: user@example.com
```

---

### **User Service API**

**Register User**
```http
POST /api/v1/users/register
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}

Response 201:
{
  "id": 1,
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "CUSTOMER"
}
```

**Login**
```http
POST /api/v1/users/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

Response 200:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

---

### **Product Service API**

**List Products**
```http
GET /api/v1/products?page=0&size=10
Authorization: Bearer <JWT>

Response 200:
{
  "content": [
    {
      "id": 1,
      "name": "Laptop",
      "price": 999.99,
      "category": "Electronics"
    }
  ],
  "totalPages": 5,
  "totalElements": 50
}
```

**Create Product (ADMIN only)**
```http
POST /api/v1/products
Authorization: Bearer <ADMIN_JWT>
Content-Type: application/json

{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "category": "Electronics"
}

Response 201:
{
  "id": 1,
  "name": "Laptop",
  "price": 999.99
}
```

---

### **Cart Service API**

**Get Cart**
```http
GET /api/v1/cart
Authorization: Bearer <JWT>

Response 200:
{
  "items": [
    {
      "productId": 1,
      "productName": "Laptop",
      "quantity": 2,
      "price": 999.99,
      "subtotal": 1999.98
    }
  ],
  "total": 1999.98
}
```

**Add Item to Cart**
```http
POST /api/v1/cart/items
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}

Response 200:
{
  "message": "Item added to cart"
}
```

---

### **Order Service API**

**Place Order**
```http
POST /api/v1/orders
Authorization: Bearer <JWT>

Response 201:
{
  "orderId": 123,
  "status": "CONFIRMED",
  "total": 1999.98,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 999.99
    }
  ],
  "createdAt": "2025-11-20T10:30:00Z"
}
```

**Get Order History**
```http
GET /api/v1/orders
Authorization: Bearer <JWT>

Response 200:
[
  {
    "orderId": 123,
    "status": "CONFIRMED",
    "total": 1999.98,
    "createdAt": "2025-11-20T10:30:00Z"
  }
]
```

---

## 🐛 Troubleshooting

### **Services Not Starting**

**Check logs**:
```bash
docker-compose logs -f <service-name>
docker-compose logs -f order-service
```

**Common Issues**:
1. **Port already in use**: Change port in docker-compose.yml
2. **Database not ready**: Wait for health checks to pass
3. **Eureka not accessible**: Ensure eureka-server is healthy

---

### **JWT Token Issues**

**Invalid signature error**:
- Ensure `JWT_SECRET` is same in api-gateway and user-service
- Default: `mySecretKeyForJWTTokenGenerationAndValidation12345678`

**Token expired**:
- Login again to get new token
- Default expiration: 1 hour

---

### **Circuit Breaker Open**

**Symptom**: Payment/Inventory calls failing with "Circuit breaker open"

**Solution**:
1. Check target service logs
2. Wait 60 seconds for circuit to go HALF-OPEN
3. Circuit will close if service recovers

**Manual reset**:
```bash
curl -X POST http://localhost:8085/actuator/circuitbreakers/paymentService/reset
```

---

### **RabbitMQ Queue Not Consuming**

**Check queue depth**:
- Open http://localhost:15672
- Login: admin/admin
- Check if messages are piling up

**Restart consumer**:
```bash
docker-compose restart notification-service
```

---

## 📈 Performance Tips

1. **Scale specific services**:
```bash
docker-compose up -d --scale product-service=3
```

2. **Monitor slow queries** in Grafana

3. **Enable Redis cache** for frequently accessed products

4. **Tune JVM heap**:
```yaml
environment:
  - JAVA_OPTS=-Xmx512m -Xms256m
```

---

## 🎓 Learning Resources

### **Microservices Patterns**
- Database per Service
- API Gateway
- Service Discovery
- Circuit Breaker
- Saga Pattern (compensating transactions)
- Event-Driven Architecture

### **Spring Cloud**
- Eureka for service discovery
- Gateway for routing
- OpenFeign for REST clients
- Config Server for centralization

### **Observability**
- Distributed tracing with Zipkin
- Metrics with Prometheus
- Dashboards with Grafana
- Log aggregation with Loki

---

## 📞 Support

For issues or questions:
1. Check logs: `docker-compose logs -f <service>`
2. Check Eureka: http://localhost:8761
3. Check Grafana metrics: http://localhost:3000

---

## 🏆 Next Steps

1. ✅ Run `docker-compose up` and explore the system
2. ✅ Test complete order flow
3. ✅ Monitor services in Grafana
4. ✅ View traces in Zipkin
5. ✅ Check RabbitMQ message flow
6. ✅ Simulate circuit breaker by stopping payment-service

---

**🚀 Happy Microservicing!**
