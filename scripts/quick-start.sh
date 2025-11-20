#!/bin/bash

# Quick Start Script
# Builds and starts all services

set -e

# Navigate to project root
cd "$(dirname "$0")/.."

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}  E-Commerce Microservices Platform${NC}"
echo -e "${BLUE}  Quick Start${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
  echo -e "${YELLOW}⚠ Docker is not running. Please start Docker Desktop.${NC}"
  exit 1
fi

echo -e "${GREEN}✓ Docker is running${NC}"
echo ""

# Build all services
echo -e "${BLUE}Step 1/3: Building all microservices...${NC}"
./scripts/build-all.sh

echo ""
echo -e "${BLUE}Step 2/3: Starting Docker containers...${NC}"
docker-compose up -d

echo ""
echo -e "${BLUE}Step 3/3: Waiting for services to be healthy...${NC}"
echo -e "${YELLOW}This may take 2-3 minutes...${NC}"
sleep 30

# Check service health
echo ""
echo -e "${GREEN}Checking service health:${NC}"

services=(
  "eureka-server:8761"
  "config-server:8888"
  "api-gateway:8080"
  "user-service:8081"
  "product-service:8082"
  "cart-service:8084"
  "order-service:8085"
)

for service in "${services[@]}"
do
  name="${service%%:*}"
  port="${service##*:}"

  if curl -s "http://localhost:$port/actuator/health" > /dev/null 2>&1; then
    echo -e "  ${GREEN}✓${NC} $name (port $port)"
  else
    echo -e "  ${YELLOW}⏳${NC} $name (port $port) - still starting..."
  fi
done

echo ""
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}  All services started!${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo -e "${BLUE}Access points:${NC}"
echo "  • API Gateway:       http://localhost:8080"
echo "  • Eureka Dashboard:  http://localhost:8761"
echo "  • Zipkin Tracing:    http://localhost:9411"
echo "  • Prometheus:        http://localhost:9090"
echo "  • Grafana:           http://localhost:3000 (admin/admin)"
echo "  • RabbitMQ:          http://localhost:15672 (admin/admin)"
echo ""
echo -e "${BLUE}Quick test:${NC}"
echo "  1. Register: curl -X POST http://localhost:8080/api/users/register \\"
echo "       -H 'Content-Type: application/json' \\"
echo "       -d '{\"email\":\"test@example.com\",\"password\":\"password123\",\"firstName\":\"John\",\"lastName\":\"Doe\"}'"
echo ""
echo "  2. Login:    curl -X POST http://localhost:8080/api/users/login \\"
echo "       -H 'Content-Type: application/json' \\"
echo "       -d '{\"email\":\"test@example.com\",\"password\":\"password123\"}'"
echo ""
echo "  3. See test-api.sh for more examples"
echo ""
echo -e "${GREEN}Happy microservicing! 🚀${NC}"
