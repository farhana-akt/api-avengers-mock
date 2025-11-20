# E-Commerce Microservices Platform

A production-ready microservices-based e-commerce platform built with Spring Boot, demonstrating industry-standard patterns, DevOps practices, and observability.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🚀 Quick Start

```bash
# One-command setup: build, start, and verify all services
./scripts/build/quick-start.sh

# Wait 2-3 minutes for services to be healthy, then populate test data
./scripts/test/populate-databases.sh

# Test the complete API flow
./scripts/test/test-api.sh

# Run load tests (requires K6)
./scripts/test/run-load-test.sh
```

**That's it!** The platform is now running with:
- ✅ All microservices
- ✅ Databases with mock data
- ✅ Complete observability stack (Grafana, Prometheus, Loki, Zipkin)
- ✅ Service discovery and configuration
- ✅ Event-driven architecture with RabbitMQ

## 📊 Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| **Frontend** | http://localhost:3001 | - |
| **API Gateway** | http://localhost:8080 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Prometheus** | http://localhost:9090 | - |
| **Zipkin** | http://localhost:9411 | - |
| **Eureka** | http://localhost:8761 | - |
| **RabbitMQ** | http://localhost:15672 | admin/admin |

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
┌──────▼────────┐  ┌──────────┐  ┌──────────┐     │
│  User Service │  │ Product  │  │   Cart   │     │
│  (Postgres)   │  │(Postgres)│  │ (Redis)  │     │
└───────────────┘  └──────────┘  └──────────┘     │
                                                  │
┌──────────────┐  ┌───────────┐  ┌───────────┐    │
│ Order Service│  │ Inventory │  │  Payment  │    │
│ (Postgres +  │  │(Postgres) │  │  Service  │    │
│ Circuit Brk) │  └───────────┘  └───────────┘    │
└───────┬──────┘                                  │
        │                                         │
        ▼                                         │
┌────────────────┐                                │
│   RabbitMQ     │ ◄──────────────────────────────┘
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
./scripts/build/quick-start.sh    # Build and start everything
./scripts/build/build-all.sh       # Build all microservices
./scripts/test/test-all.sh        # Run all unit tests
./scripts/test/test-api.sh        # Test complete order flow
./scripts/build/stop-all.sh        # Stop all services
```

## Testing

### Unit Tests (Mockito)
```bash
# Test all services
./scripts/test/test-all.sh

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

## 📊 Observability & Monitoring

This platform includes a **complete observability stack** with pre-configured dashboards:

### Grafana Dashboards
- **Microservices Overview** - Request rates, response times, success rates, resource usage
- **JVM Metrics** - Memory, GC, threads for each service
- **Custom dashboards** - Build your own with PromQL

### Distributed Tracing
- **Zipkin** integration on all services
- Trace IDs in logs for correlation
- View complete request flows across services

### Log Aggregation
- **Loki** for centralized log storage
- **Promtail** automatically collects Docker logs
- Query logs by service, time, or trace ID

**📖 See [MONITORING.md](MONITORING.md) for detailed monitoring guide**

## 🧪 Load Testing

Test platform performance with K6:

```bash
# Install K6 (macOS)
brew install k6

# Run comprehensive load test
./scripts/test/run-load-test.sh
```

The load test simulates:
- **User registration & login** (30% of traffic)
- **Product browsing** (40% of traffic)
- **Complete purchase flow** (30% of traffic)

Load profile:
- Ramp up to 50 users over 1.5 minutes
- Sustain 50 users for 2 minutes
- Ramp up to 100 users
- Sustain 100 users for 2 minutes
- Ramp down

### Monitoring During Load Tests

1. Open Grafana (http://localhost:3000)
2. Go to **Microservices Overview** dashboard
3. Run load test: `./scripts/test/run-load-test.sh`
4. Watch real-time metrics:
   - Request rate increasing
   - Response time behavior
   - Error rates
   - Resource usage (CPU, memory)
   - GC activity

**Expected results:**
- ✅ P95 response time < 500ms
- ✅ Success rate > 99%
- ✅ No service failures
- ✅ Stable memory usage

## 🔄 CI/CD Pipeline

GitHub Actions workflow with intelligent change detection:

### Features
- ✅ **Selective building** - Only builds changed services
- ✅ **Automated testing** - Runs tests on every PR
- ✅ **Docker image versioning** - Semantic versioning with commit SHA
- ✅ **Docker Hub integration** - Pushes to `smamm/ecommerce-*`
- ✅ **Build caching** - Faster subsequent builds

### Setup

1. **Add GitHub Secrets:**
   - `DOCKER_USERNAME`: `smamm`
   - `DOCKER_PASSWORD`: Your Docker Hub access token

2. **Push to main branch** - Automatically builds and pushes changed services

3. **View status** - Check GitHub Actions tab

**📖 See [.github/GITHUB_SETUP.md](.github/GITHUB_SETUP.md) for detailed CI/CD setup**

### Image Naming Convention
```
smamm/ecommerce-<service>:v1.0.<build>-<sha>
smamm/ecommerce-<service>:latest
```

Example:
```bash
docker pull smamm/ecommerce-user-service:v1.0.42-a3f2e1c
docker pull smamm/ecommerce-api-gateway:latest
```

## 📁 Project Structure

```
.
├── .github/
│   └── workflows/
│       └── ci-cd.yml              # GitHub Actions CI/CD pipeline
├── scripts/
│   ├── quick-start.sh             # One-command startup
│   ├── build-all.sh               # Build all Docker images
│   ├── populate-databases.sh      # Load mock data
│   ├── run-load-test.sh           # Execute K6 load tests
│   └── load-test.js               # K6 test scenarios
├── database/
│   └── init-scripts/              # SQL scripts for mock data
├── grafana/
│   └── provisioning/
│       ├── datasources/           # Auto-configured datasources
│       └── dashboards/            # Pre-built dashboards
├── eureka-server/                 # Service discovery
├── config-server/                 # Centralized configuration
├── api-gateway/                   # Entry point with JWT
├── user-service/                  # Authentication & users
├── product-service/               # Product catalog
├── inventory-service/             # Stock management
├── cart-service/                  # Shopping cart (Redis)
├── order-service/                 # Order orchestration (Saga)
├── payment-service/               # Payment processing
├── notification-service/          # Notifications
├── frontend/                      # Web UI
├── docker-compose.yml             # Full stack orchestration
├── prometheus.yml                 # Metrics scraping config
├── loki-config.yml               # Log aggregation config
└── MONITORING.md                 # Comprehensive monitoring guide
```

## 🎯 Mock Data

The platform includes realistic mock data:

- **50 products** across 5 categories:
  - Electronics (10 items)
  - Fashion (10 items)
  - Home & Garden (10 items)
  - Sports (10 items)
  - Books (10 items)

- **Inventory levels** with realistic stock:
  - High stock items (500+ units)
  - Medium stock items (100-300 units)
  - Low stock items (< 100 units)
  - Reserved quantities for pending orders

Load data:
```bash
./scripts/test/populate-databases.sh
```

## 🛠️ Development

### Build Individual Service
```bash
cd user-service
mvn clean package
docker build -t user-service .
```

### Run Without Docker
```bash
# Start infrastructure (databases, eureka, etc.)
docker-compose --profile infrastructure --profile databases up -d

# Run service locally
cd user-service
mvn spring-boot:run
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f user-service

# With grep
docker-compose logs -f | grep ERROR
```

### Clean Up
```bash
# Stop all services
docker-compose --profile full down

# Remove volumes (deletes data)
docker-compose --profile full down -v

# Clean Docker system
docker system prune -a
```

## 🐛 Troubleshooting

### Services Won't Start
```bash
# Check Docker is running
docker info

# Check ports are free
lsof -i :8080
lsof -i :5432

# View service logs
docker-compose logs <service-name>
```

### Grafana Shows No Data
1. Check datasources: Configuration > Data Sources
2. Click "Save & Test" on each datasource
3. Verify services are exposing metrics: `curl http://localhost:8081/actuator/prometheus`

### Load Test Fails
```bash
# Verify K6 is installed
k6 version

# Check API Gateway is reachable
curl http://localhost:8080/actuator/health

# Ensure databases have data
./scripts/test/populate-databases.sh
```

### Build Errors
```bash
# Clean Maven cache
cd <service>
mvn clean

# Remove old Docker images
docker-compose --profile full down --rmi all

# Rebuild
./scripts/build/build-all.sh
```

## 📚 Additional Documentation

- **[USAGE.md](USAGE.md)** - Complete usage guide (API examples, monitoring, load testing)
- **[.github/README.md](.github/README.md)** - CI/CD pipeline setup & build process details
- **[API Documentation](http://localhost:8080/swagger-ui.html)** - Interactive Swagger docs (when running)

## 🆕 Recent Improvements

**Monitoring & Observability:**
- ✅ Fixed Loki log aggregation with proper configuration & Docker socket access
- ✅ Auto-configured Grafana with 3 datasources (Prometheus, Loki, Zipkin)
- ✅ 2 pre-built dashboards: Microservices Overview & JVM Metrics
- ✅ Enhanced Zipkin tracing with trace IDs in logs for correlation

**Testing & Performance:**
- ✅ K6 load testing with realistic scenarios (30% login, 40% browse, 30% purchase)
- ✅ Automated test script with gradual ramp-up: 10→50→100 users over 7 minutes
- ✅ Real-time monitoring integration - watch metrics during load tests

**CI/CD & Deployment:**
- ✅ GitHub Actions with smart change detection (only builds modified services)
- ✅ Comprehensive testing: Maven compile + JUnit 5/Mockito tests + Docker build
- ✅ Automated Docker Hub publishing with semantic versioning (v1.0.build-sha)
- ✅ Selective building saves 15-20 minutes per run

**Data & Scripts:**
- ✅ 50 mock products across 5 categories (Electronics, Fashion, Home, Sports, Books)
- ✅ Realistic inventory scenarios (high/medium/low stock)
- ✅ One-command database population: `./scripts/test/populate-databases.sh`

## 🎓 Learning Resources

This project demonstrates:
- Microservices architecture patterns
- Service discovery with Eureka
- API Gateway with JWT authentication
- Event-driven architecture (RabbitMQ)
- Saga pattern for distributed transactions
- Circuit breaker with Resilience4j
- Distributed tracing (Zipkin)
- Metrics & dashboards (Prometheus & Grafana)
- Log aggregation (Loki)
- Docker multi-stage builds
- GitHub Actions CI/CD with change detection
- Load testing with K6

## 📋 Detailed Changes Log

### Files Created
- `loki-config.yml` - Loki log aggregation configuration
- `grafana/provisioning/datasources/datasources.yml` - Auto-configured datasources
- `grafana/provisioning/dashboards/` - 2 pre-built dashboards (Microservices Overview, JVM Metrics)
- `scripts/load-test.js` - K6 load testing with 3 scenarios
- `scripts/run-load-test.sh` - Load test runner
- `scripts/populate-databases.sh` - Database population automation
- `database/init-scripts/` - SQL scripts for mock data (50 products + inventory)
- `.github/workflows/main-cicd.yml` - Reorganized CI/CD pipeline
- `.github/README.md` - Comprehensive CI/CD documentation

### Files Modified
- `docker-compose.yml` - Added Loki config, Promtail Docker socket, Grafana provisioning
- `user-service/src/main/resources/application-docker.yml` - Enhanced tracing with trace IDs
- `README.md` - Added improvements section and reorganized
- `USAGE.md` - Merged monitoring content, added load testing guide

### Files Deleted
- `MONITORING.md` - Merged into USAGE.md
- Old workflow file - Replaced with better organized version

### Key Technical Details
- **Loki**: Now properly configured with filesystem storage and Docker logs collection
- **Grafana**: Auto-provisions on startup (no manual setup needed)
- **Zipkin**: Logs include `[service,traceId,spanId]` for correlation
- **K6 Tests**: Realistic user journeys with gradual load ramp-up
- **CI/CD**: Smart change detection saves 15-20 min per build
- **Docker**: Two-stage builds (Maven → JRE) for minimal images
- **Versioning**: `v1.0.<build>-<sha>` + `latest` tags

## License

MIT License - see [LICENSE](LICENSE) file for details.

## 👥 Contributing

Contributions are welcome! Feel free to:
- Report bugs
- Suggest features
- Submit pull requests
- Improve documentation

## 📞 Support

- **Issues:** [GitHub Issues](https://github.com/your-repo/issues)
- **Check service health:** http://localhost:8761 (Eureka)
- **View logs:** `docker-compose logs -f <service-name>`
- **Monitoring:** http://localhost:3000 (Grafana)

---

**Built for learning, hackathons, and showcasing microservices best practices.**

⭐ Star this repo if you find it helpful!
