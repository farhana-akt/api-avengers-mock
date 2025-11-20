# E-Commerce Microservices Platform

A production-ready microservices-based e-commerce platform built with Spring Boot, demonstrating industry-standard patterns, DevOps practices, and observability.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## Quick Start

```bash
# Build all services
./scripts/build-all.sh

# Start all services with Docker Compose
docker-compose up -d

# Check service health (wait 2-3 minutes)
curl http://localhost:8080/actuator/health

# Test the complete API flow
./scripts/test-api.sh
```

**That's it!** The platform is now running with all services, databases, and monitoring tools.

## Architecture Overview

```
┌─────────────┐
│   Client    │
│  (Browser)  │
└──────┬──────┘
       │
┌──────▼──────────┐
│  API Gateway    │ ──► JWT Validation
│   Port 8080     │ ──► Routing
└──────┬──────────┘
       │
       ├──────────────────────────────────────────┐
       │                                          │
┌──────▼────────┐  ┌──────────┐  ┌──────────┐  │
│  User Service │  │ Product  │  │   Cart   │  │
│  (Postgres)   │  │ (Postgres)│  │ (Redis)  │  │
└───────────────┘  └──────────┘  └──────────┘  │
                                                 │
┌──────────────┐  ┌───────────┐  ┌───────────┐ │
│ Order Service│  │ Inventory │  │  Payment  │ │
│ (Postgres +  │  │(Postgres) │  │  Service  │ │
│ Circuit Brk) │  └───────────┘  └───────────┘ │
└───────┬──────┘                                │
        │                                        │
        ▼                                        │
┌────────────────┐                              │
│   RabbitMQ     │ ◄────────────────────────────┘
│  (Event Bus)   │
└────────┬───────┘
         │
         ▼
┌────────────────┐
│ Notification   │
│   Service      │
└────────────────┘
```

**Service Discovery:** All services register with Eureka Server
**Configuration:** Centralized via Config Server
**Observability:** Zipkin (tracing) + Prometheus (metrics) + Grafana (dashboards)

## Tech Stack

| Category | Technologies |
|----------|-------------|
| **Framework** | Spring Boot 3.2.0, Spring Cloud 2023.0.0 |
| **Language** | Java 17 |
| **Databases** | PostgreSQL 14, Redis 7 |
| **Messaging** | RabbitMQ 3.12 |
| **API Docs** | Swagger/OpenAPI 2.2.0 |
| **Monitoring** | Zipkin, Prometheus, Grafana, Loki |
| **Testing** | JUnit 5, Mockito, K6 Load Testing |
| **DevOps** | Docker, Docker Compose |

## Services & Ports

| Service | Port | Swagger UI | Description |
|---------|------|------------|-------------|
| **API Gateway** | 8080 | [/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Entry point, JWT validation, routing |
| **Eureka Server** | 8761 | - | Service discovery dashboard |
| **Config Server** | 8888 | - | Centralized configuration |
| **User Service** | 8081 | [/swagger-ui.html](http://localhost:8081/swagger-ui.html) | Authentication, user management |
| **Product Service** | 8082 | [/swagger-ui.html](http://localhost:8082/swagger-ui.html) | Product catalog |
| **Inventory Service** | 8083 | [/swagger-ui.html](http://localhost:8083/swagger-ui.html) | Stock management |
| **Cart Service** | 8084 | [/swagger-ui.html](http://localhost:8084/swagger-ui.html) | Shopping cart (Redis) |
| **Order Service** | 8085 | [/swagger-ui.html](http://localhost:8085/swagger-ui.html) | Order processing with circuit breaker |
| **Payment Service** | 8086 | [/swagger-ui.html](http://localhost:8086/swagger-ui.html) | Payment processing |
| **Notification Service** | 8087 | - | Email/SMS notifications |

### Monitoring Tools

- **Zipkin**: [http://localhost:9411](http://localhost:9411) - Distributed tracing
- **Prometheus**: [http://localhost:9090](http://localhost:9090) - Metrics
- **Grafana**: [http://localhost:3000](http://localhost:3000) - Dashboards (admin/admin)
- **RabbitMQ**: [http://localhost:15672](http://localhost:15672) - Message queue (admin/admin)

## Key Features

- **Microservices Architecture**: 10 independent services with database-per-service pattern
- **Security**: JWT authentication with BCrypt password hashing
- **Resilience**: Circuit breaker, retry, fallback, saga pattern for compensating transactions
- **Event-Driven**: RabbitMQ for asynchronous communication
- **API Documentation**: Swagger/OpenAPI for all REST APIs
- **Observability**: Distributed tracing, metrics, logs, dashboards
- **Testing**: Comprehensive Mockito unit tests + K6 load tests
- **Frontend**: Vanilla JavaScript single-page application
- **Mock Data**: 50 products, 10 users, inventory data

## Usage

For detailed documentation, see **[USAGE.md](USAGE.md)** which covers:

- Prerequisites and setup
- Build and run instructions
- API examples and workflows
- Testing (unit tests, load tests)
- Swagger/OpenAPI usage
- Monitoring and observability
- Frontend application
- Database mock data
- Troubleshooting

## Scripts

All automation scripts are in the `/scripts` directory:

```bash
./scripts/quick-start.sh    # Build and start everything
./scripts/build-all.sh       # Build all microservices
./scripts/test-all.sh        # Run all unit tests
./scripts/test-api.sh        # Test complete order flow
./scripts/stop-all.sh        # Stop all services
```

## Testing

### Unit Tests (Mockito)
```bash
# Test all services
./scripts/test-all.sh

# Test specific service
cd user-service && mvn test
```

### Load Testing (K6)
```bash
# Install k6: https://k6.io/docs/getting-started/installation/

# Test order flow
k6 run k6/load-test-orders.js

# Test product browsing
k6 run k6/load-test-products.js

# Test authentication
k6 run k6/load-test-auth.js

# Test complete flow
k6 run k6/load-test-full-flow.js
```

## Frontend

A simple vanilla JavaScript frontend is available at `/frontend`:

```bash
# Serve the frontend (requires Python)
cd frontend
python3 -m http.server 8000

# Or use any HTTP server
npx serve

# Open http://localhost:8000
```

Features: Login/Register, Product browsing, Shopping cart, Order placement

## Sample Data

Load sample data into databases:

```bash
# Load products (50 items across 5 categories)
docker exec -i postgres-product psql -U postgres -d productdb < database/init/products.sql

# Load inventory
docker exec -i postgres-inventory psql -U postgres -d inventorydb < database/init/inventory.sql

# Load users (password: password123)
docker exec -i postgres-user psql -U postgres -d userdb < database/init/users.sql
```

Sample credentials:
- **Admin**: admin@ecommerce.com / password123
- **Customer**: john.doe@example.com / password123

## Architecture Patterns

1. **API Gateway** - Single entry point with JWT validation
2. **Service Discovery** - Eureka for dynamic service registration
3. **Database Per Service** - Each service has its own database
4. **Circuit Breaker** - Resilience4j prevents cascade failures
5. **Event-Driven** - RabbitMQ for asynchronous events
6. **Saga Pattern** - Compensating transactions for order failures

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Support

- See [USAGE.md](USAGE.md) for detailed guide
- Check service logs: `docker-compose logs -f <service-name>`
- Eureka dashboard: http://localhost:8761
- Grafana: http://localhost:3000

**Built for learning, hackathons, and showcasing microservices best practices.**
