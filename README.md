# 🚀 E-Commerce Microservices Platform

A complete, production-ready microservices-based e-commerce platform built with Spring Boot, demonstrating industry-standard DevOps practices, observability, and resilience patterns.

![Architecture](https://img.shields.io/badge/Architecture-Microservices-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🌟 Key Features

### **Microservices Architecture**
- ✅ 9 independent microservices
- ✅ Database per service pattern
- ✅ Service discovery with Eureka
- ✅ Centralized configuration with Config Server
- ✅ API Gateway with intelligent routing

### **Security**
- ✅ JWT-based authentication
- ✅ Gateway validates JWT once (best practice)
- ✅ Services trust internal headers
- ✅ BCrypt password hashing

### **Resilience & Fault Tolerance**
- ✅ Circuit breaker pattern (Resilience4j)
- ✅ Retry mechanisms
- ✅ Fallback methods
- ✅ Compensating transactions (Saga pattern)

### **Event-Driven Architecture**
- ✅ RabbitMQ for asynchronous communication
- ✅ Event sourcing for notifications
- ✅ Decoupled services

### **Observability Stack**
- ✅ **Zipkin** - Distributed tracing
- ✅ **Prometheus** - Metrics collection
- ✅ **Grafana** - Dashboards and visualization
- ✅ **Loki** - Log aggregation

### **Data Management**
- ✅ PostgreSQL for persistent data
- ✅ Redis for caching (cart service)
- ✅ Flyway for database migrations
- ✅ JPA/Hibernate for ORM

### **Professional DevOps**
- ✅ Docker containerization
- ✅ Docker Compose orchestration
- ✅ Health checks for all services
- ✅ Multi-stage Docker builds
- ✅ GitHub Actions CI/CD pipeline

---

## 📦 Microservices

| Service | Port | Purpose |
|---------|------|---------|
| **Eureka Server** | 8761 | Service Discovery |
| **Config Server** | 8888 | Centralized Configuration |
| **API Gateway** | 8080 | Entry Point, JWT Validation |
| **User Service** | 8081 | Authentication, User Management |
| **Product Service** | 8082 | Product Catalog |
| **Inventory Service** | 8083 | Stock Management |
| **Cart Service** | 8084 | Shopping Cart (Redis) |
| **Order Service** | 8085 | Order Processing, Orchestration |
| **Payment Service** | 8086 | Payment Processing |
| **Notification Service** | 8087 | Email/SMS Notifications |

---

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **Language**: Java 17
- **Build Tool**: Maven 3.9
- **Databases**: PostgreSQL 14, Redis 7
- **Message Queue**: RabbitMQ 3.12
- **Tracing**: Zipkin
- **Metrics**: Prometheus, Grafana
- **Logs**: Loki
- **Containerization**: Docker, Docker Compose

---

## 🚀 Quick Start

### **Prerequisites**
- Docker Desktop installed and running
- 8GB+ RAM available
- Ports 8080-8090, 5432-5435, 6379, 5672, 9090, 3000, 9411 available

### **Step 1: Clone Repository**
```bash
git clone <repository-url>
cd api-avengers-mock
```

### **Step 2: Build All Services**
```bash
# Build each microservice
./build-all.sh
```

Or manually:
```bash
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

### **Step 3: Start All Services**
```bash
docker-compose up -d
```

### **Step 4: Wait for Services to Start**
```bash
# This takes 2-3 minutes for all services to be healthy
docker-compose ps

# Watch logs
docker-compose logs -f
```

### **Step 5: Verify Services**
```bash
# Check Eureka Dashboard - All services should be registered
open http://localhost:8761

# Check API Gateway
curl http://localhost:8080/actuator/health

# Check Grafana (login: admin/admin)
open http://localhost:3000

# Check Zipkin
open http://localhost:9411

# Check RabbitMQ (login: admin/admin)
open http://localhost:15672
```

---

## 🧪 Testing the System

### **1. Register a New User**
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

### **2. Login and Get JWT Token**
```bash
curl -X POST http://localhost:8080/api/v1/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'

# Save the token from response
export JWT_TOKEN="<token-from-response>"
```

### **3. Browse Products**
```bash
curl -X GET http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### **4. Add Product to Cart**
```bash
curl -X POST http://localhost:8080/api/v1/cart/items \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

### **5. View Cart**
```bash
curl -X GET http://localhost:8080/api/v1/cart \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### **6. Place Order**
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### **7. View Order History**
```bash
curl -X GET http://localhost:8080/api/v1/orders \
  -H "Authorization: Bearer $JWT_TOKEN"
```

---

## 📊 Monitoring & Observability

### **Zipkin - Distributed Tracing**
- **URL**: http://localhost:9411
- **Purpose**: Track requests across microservices
- **Features**: Latency analysis, dependency graph, error traces

### **Prometheus - Metrics**
- **URL**: http://localhost:9090
- **Purpose**: Collect and query metrics
- **Metrics**: Request rate, error rate, latency, JVM stats

### **Grafana - Dashboards**
- **URL**: http://localhost:3000
- **Login**: admin / admin
- **Purpose**: Visualize metrics and logs
- **Dashboards**: Microservices overview, JVM stats, database metrics

### **RabbitMQ Management**
- **URL**: http://localhost:15672
- **Login**: admin / admin
- **Purpose**: Monitor message queues
- **Features**: Queue depth, message rates, consumer status

---

## 🏗️ Architecture Patterns

### **1. Database Per Service**
Each microservice has its own database, ensuring loose coupling and independent scaling.

### **2. API Gateway Pattern**
Single entry point with JWT validation. Services trust gateway and read user context from headers.

### **3. Service Discovery**
Eureka enables dynamic service registration and discovery. No hardcoded URLs.

### **4. Circuit Breaker**
Resilience4j prevents cascade failures. Fallback methods handle failures gracefully.

### **5. Event-Driven Architecture**
RabbitMQ enables asynchronous communication. Order and Payment services publish events.

### **6. Compensating Transactions (Saga)**
Order Service releases inventory if payment fails (compensating transaction).

---

## 🔒 Security Architecture

### **JWT Authentication Flow**

1. **User Login**:
   - User sends credentials to API Gateway
   - Gateway forwards to User Service
   - User Service validates and generates JWT
   - JWT returned to user

2. **Authenticated Requests**:
   - User sends: `Authorization: Bearer <JWT>`
   - API Gateway validates JWT signature
   - Gateway extracts userId, role, email
   - Gateway adds headers: `X-User-Id`, `X-User-Role`, `X-User-Email`
   - Services trust these headers (no JWT validation)

### **Why This Pattern?**
✅ **Security**: JWT validated once at edge
✅ **Performance**: Services don't parse JWT (faster)
✅ **Scalability**: Lightweight services
✅ **Simplicity**: Services just read headers

---

## 📚 Documentation

For detailed implementation guide, see [IMPLEMENTATION.md](IMPLEMENTATION.md)

Topics covered:
- Architecture overview
- Service details
- Port mappings
- API documentation
- Testing strategies
- Troubleshooting
- Performance tuning

---

## 🧪 Running Tests

### **Unit Tests**
```bash
cd <service-directory>
mvn test
```

### **Integration Tests**
```bash
mvn verify
```

### **All Tests**
```bash
./run-tests.sh
```

---

## 🐛 Troubleshooting

### **Services Not Starting**
```bash
# Check logs
docker-compose logs -f <service-name>

# Restart specific service
docker-compose restart <service-name>

# Rebuild and restart
docker-compose up -d --build <service-name>
```

### **Port Conflicts**
Check if ports are already in use:
```bash
lsof -i :8080
lsof -i :5432
```

### **Database Issues**
```bash
# Reset databases
docker-compose down -v
docker-compose up -d
```

### **Clear Everything and Start Fresh**
```bash
docker-compose down -v
docker system prune -a
docker-compose up -d --build
```

---

## 📈 Scaling Services

Scale specific services:
```bash
# Scale product service to 3 instances
docker-compose up -d --scale product-service=3

# Scale order service to 2 instances
docker-compose up -d --scale order-service=2
```

Eureka will automatically register all instances.

---

## 🎓 Learning Outcomes

By exploring this project, you'll learn:

1. **Microservices Architecture**
   - Service decomposition
   - Inter-service communication
   - Data management patterns

2. **Spring Cloud Ecosystem**
   - Eureka for service discovery
   - Gateway for routing
   - Config Server for configuration
   - OpenFeign for REST clients

3. **Resilience Patterns**
   - Circuit breaker
   - Retry mechanisms
   - Fallback methods
   - Compensating transactions

4. **Observability**
   - Distributed tracing
   - Metrics collection
   - Log aggregation
   - Dashboard creation

5. **DevOps Practices**
   - Containerization
   - Orchestration
   - Health checks
   - CI/CD pipelines

6. **Security**
   - JWT authentication
   - Gateway pattern
   - Password hashing
   - Secure communication

---

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

Built with:
- Spring Boot & Spring Cloud
- Netflix OSS (Eureka, Resilience4j)
- Grafana Labs (Grafana, Loki)
- CNCF (Prometheus, OpenTelemetry)
- Docker

---

## 📞 Support

For questions or issues:
- Check [IMPLEMENTATION.md](IMPLEMENTATION.md)
- Review service logs: `docker-compose logs -f <service>`
- Check Eureka dashboard: http://localhost:8761
- Monitor metrics in Grafana: http://localhost:3000

---

**🚀 Happy Microservicing! Built for hackathons and learning.**
