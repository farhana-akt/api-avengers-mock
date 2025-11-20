#!/bin/bash

# Build All Microservices Script
# This script builds all Spring Boot microservices

set -e

# Navigate to project root
cd "$(dirname "$0")/.."

echo "========================================="
echo "Building All Microservices"
echo "========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

services=(
  "eureka-server"
  "config-server"
  "api-gateway"
  "user-service"
  "product-service"
  "inventory-service"
  "cart-service"
  "order-service"
  "payment-service"
  "notification-service"
)

for service in "${services[@]}"
do
  echo -e "${BLUE}Building $service...${NC}"
  cd "$service" || exit
  mvn clean package -DskipTests
  echo -e "${GREEN}✓ $service built successfully${NC}"
  echo ""
  cd ..
done

echo "========================================="
echo -e "${GREEN}All services built successfully!${NC}"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Run: docker-compose up -d"
echo "2. Wait 2-3 minutes for services to start"
echo "3. Check Eureka: http://localhost:8761"
echo "4. Test API: http://localhost:8080"
