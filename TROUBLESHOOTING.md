# 🔧 Troubleshooting Guide

Common issues and solutions for the E-Commerce Microservices Platform.

## 🚨 Common Issues

### 1. Services Not Starting

**Symptom:** Docker containers fail to start or crash immediately

**Solutions:**

**Check logs:**
```bash
docker-compose logs -f <service-name>
docker-compose logs -f user-service
```

**Check container status:**
```bash
docker-compose ps
```

**Restart specific service:**
```bash
docker-compose restart <service-name>
```

**Rebuild and restart:**
```bash
docker-compose up -d --build <service-name>
```

---

### 2. Port Already in Use

**Symptom:** Error: "port is already allocated"

**Solutions:**

**Find process using port:**
```bash
# macOS/Linux
lsof -i :8080
lsof -i :5432

# Windows
netstat -ano | findstr :8080
```

**Kill the process:**
```bash
kill -9 <PID>
```

**Or change port in docker-compose.yml:**
```yaml
services:
  api-gateway:
    ports:
      - "8090:8080"  # Change 8080 to 8090 externally
```

---

### 3. Eureka Dashboard Shows No Services

**Symptom:** http://localhost:8761 shows no registered services

**Solutions:**

**Check Eureka logs:**
```bash
docker-compose logs -f eureka-server
```

**Verify services are configured correctly:**
- Check `application-docker.yml` has correct Eureka URL
- Ensure `eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka/`

**Restart services:**
```bash
docker-compose restart eureka-server
docker-compose restart user-service product-service
```

**Wait for registration (takes 30-60 seconds):**
- Services send heartbeat every 30 seconds
- Full registration takes 3 heartbeats

---

### 4. JWT Token Issues

**Symptom:** "Invalid JWT signature" or "Token expired"

**Solutions:**

**Verify JWT secret is consistent:**
- Check `JWT_SECRET` in docker-compose.yml
- Should be same for api-gateway and user-service
- Default: `mySecretKeyForJWTTokenGenerationAndValidation12345678`

**Get a fresh token:**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

**Check token expiration:**
- Default: 1 hour
- Adjust in User Service if needed

---

### 5. Database Connection Errors

**Symptom:** "Connection refused" or "Could not connect to database"

**Solutions:**

**Check database containers:**
```bash
docker-compose ps postgres-user postgres-product postgres-order postgres-inventory
```

**Check database logs:**
```bash
docker-compose logs -f postgres-user
```

**Verify database health:**
```bash
docker exec -it postgres-user pg_isready -U postgres
```

**Connect to database manually:**
```bash
docker exec -it postgres-user psql -U postgres -d userdb
```

**Reset databases:**
```bash
docker-compose down -v  # WARNING: This deletes all data
docker-compose up -d
```

---

### 6. Redis Connection Issues (Cart Service)

**Symptom:** Cart service fails with "Connection refused" to Redis

**Solutions:**

**Check Redis:**
```bash
docker-compose logs -f redis
```

**Test Redis connection:**
```bash
docker exec -it redis-cart redis-cli ping
# Should return: PONG
```

**Check Redis keys:**
```bash
docker exec -it redis-cart redis-cli
> KEYS cart:*
> GET cart:user:1
```

**Restart Redis:**
```bash
docker-compose restart redis
docker-compose restart cart-service
```

---

### 7. RabbitMQ Connection Errors

**Symptom:** "Connection refused" or messages not being consumed

**Solutions:**

**Check RabbitMQ:**
```bash
docker-compose logs -f rabbitmq
```

**Access RabbitMQ Management:**
- URL: http://localhost:15672
- Credentials: admin/admin
- Check queues, exchanges, bindings

**Verify queues exist:**
- `order-events-queue`
- `payment-events-queue`

**Check message flow:**
- Publish a test message
- Verify consumer is connected
- Check message rate in UI

**Restart RabbitMQ:**
```bash
docker-compose restart rabbitmq
docker-compose restart order-service payment-service notification-service
```

---

### 8. Circuit Breaker Issues

**Symptom:** "Circuit breaker is open" errors in Order Service

**Solutions:**

**Check target service health:**
```bash
curl http://localhost:8086/actuator/health  # Payment Service
curl http://localhost:8083/actuator/health  # Inventory Service
```

**View circuit breaker state:**
```bash
curl http://localhost:8085/actuator/circuitbreakers
```

**Wait for circuit to close:**
- Circuit opens after 50% failures in 10 requests
- Waits 60 seconds before trying again (HALF_OPEN)
- After 3 successful requests, closes

**Manual reset (if available):**
```bash
curl -X POST http://localhost:8085/actuator/circuitbreakers/paymentService/reset
```

**Check Resilience4j metrics:**
```bash
curl http://localhost:8085/actuator/metrics/resilience4j.circuitbreaker.state
```

---

### 9. Zipkin Not Showing Traces

**Symptom:** No traces visible in Zipkin UI

**Solutions:**

**Check Zipkin:**
```bash
docker-compose logs -f zipkin
```

**Verify services are sending traces:**
```bash
# Check service logs for trace IDs
docker-compose logs order-service | grep traceId
```

**Check Zipkin configuration:**
- Ensure `management.zipkin.tracing.endpoint=http://zipkin:9411` in all services
- Verify `management.tracing.sampling.probability=1.0` (100% sampling)

**Test trace manually:**
1. Make an API request
2. Open http://localhost:9411
3. Click "Run Query"
4. Should see recent traces

**Restart Zipkin:**
```bash
docker-compose restart zipkin
```

---

### 10. Prometheus Not Collecting Metrics

**Symptom:** No metrics in Prometheus or Grafana

**Solutions:**

**Check Prometheus:**
```bash
docker-compose logs -f prometheus
```

**Verify Prometheus targets:**
- Open http://localhost:9090
- Status → Targets
- All services should be "UP"

**Check service metrics endpoints:**
```bash
curl http://localhost:8081/actuator/prometheus  # User Service
curl http://localhost:8085/actuator/prometheus  # Order Service
```

**Verify prometheus.yml configuration:**
```yaml
scrape_configs:
  - job_name: 'user-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['user-service:8081']
```

**Restart Prometheus:**
```bash
docker-compose restart prometheus grafana
```

---

### 11. Grafana Dashboard Not Loading

**Symptom:** Grafana shows no data or can't connect to Prometheus

**Solutions:**

**Check Grafana logs:**
```bash
docker-compose logs -f grafana
```

**Add Prometheus data source:**
1. Open http://localhost:3000 (admin/admin)
2. Configuration → Data Sources
3. Add Prometheus
4. URL: `http://prometheus:9090`
5. Save & Test

**Import dashboards:**
1. Create → Import
2. Use dashboard ID or JSON
3. Select Prometheus as data source

**Check Grafana-Prometheus connectivity:**
```bash
docker exec -it grafana curl http://prometheus:9090/api/v1/status/config
```

---

### 12. Build Failures

**Symptom:** Maven build fails

**Solutions:**

**Clear Maven cache:**
```bash
cd <service>
mvn clean
rm -rf ~/.m2/repository
mvn clean install
```

**Check Java version:**
```bash
java -version  # Should be 17
```

**Build specific service:**
```bash
cd user-service
mvn clean package -DskipTests
```

**Build with verbose output:**
```bash
mvn clean package -X
```

---

### 13. Order Placement Fails

**Symptom:** POST /api/orders returns error

**Common Causes:**

**1. Empty Cart:**
```bash
# Add item to cart first
curl -X POST http://localhost:8080/api/cart/add \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2}'
```

**2. Insufficient Stock:**
```bash
# Check inventory
curl http://localhost:8080/api/inventory/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**3. Payment Service Down:**
```bash
# Check payment service
curl http://localhost:8086/actuator/health
```

**4. Circuit Breaker Open:**
- Wait 60 seconds for circuit to retry
- Check payment service logs

**Check Order Service logs:**
```bash
docker-compose logs -f order-service
```

---

### 14. High Memory Usage

**Symptom:** Docker consuming too much memory

**Solutions:**

**Check container memory:**
```bash
docker stats
```

**Limit memory in docker-compose.yml:**
```yaml
services:
  order-service:
    deploy:
      resources:
        limits:
          memory: 512M
        reservations:
          memory: 256M
```

**Adjust JVM heap size:**
```yaml
environment:
  - JAVA_OPTS=-Xmx512m -Xms256m
```

**Stop unused services:**
```bash
docker-compose stop prometheus grafana loki
```

---

### 15. Docker Compose Issues

**Symptom:** docker-compose up fails

**Solutions:**

**Check Docker is running:**
```bash
docker info
```

**Update Docker Compose:**
```bash
docker-compose --version
# Should be 2.x or higher
```

**Validate docker-compose.yml:**
```bash
docker-compose config
```

**Clean and restart:**
```bash
docker-compose down -v
docker system prune -a
docker-compose up -d --build
```

**Check resource limits:**
- Ensure Docker has at least 8GB RAM
- Docker Desktop → Preferences → Resources

---

## 🧹 Complete Reset

If nothing works, perform a complete reset:

```bash
# 1. Stop all containers
docker-compose down -v

# 2. Remove all containers, images, volumes
docker system prune -a -f
docker volume prune -f

# 3. Rebuild everything
./build-all.sh
docker-compose up -d --build

# 4. Wait for services to start (3-5 minutes)
docker-compose logs -f

# 5. Verify services
curl http://localhost:8761  # Eureka
curl http://localhost:8080/actuator/health  # API Gateway
```

---

## 📝 Debugging Tips

### Enable Debug Logging

Add to `application-docker.yml`:
```yaml
logging:
  level:
    com.ecommerce: DEBUG
    org.springframework.cloud: DEBUG
    org.springframework.web: DEBUG
```

### View Container Logs in Real-Time
```bash
docker-compose logs -f --tail=100 <service-name>
```

### Check All Service Health
```bash
for port in 8080 8081 8082 8083 8084 8085 8086 8087 8761 8888; do
  echo "Port $port: $(curl -s http://localhost:$port/actuator/health | jq -r .status)"
done
```

### Monitor Resource Usage
```bash
docker stats --no-stream
```

### Access Container Shell
```bash
docker exec -it <container-name> /bin/sh
```

---

## 🆘 Getting Help

If you're still stuck:

1. **Check logs**: Always start with `docker-compose logs -f <service>`
2. **Verify configuration**: Review `application-docker.yml` and `docker-compose.yml`
3. **Test connectivity**: Use `curl` to test service endpoints
4. **Check Eureka**: Ensure services are registered
5. **Monitor metrics**: Use Grafana to identify bottlenecks
6. **Review traces**: Use Zipkin to see request flow

---

## 📚 Additional Resources

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Eureka Documentation](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)

---

**Still having issues? Check the service-specific logs and error messages for more details.**
