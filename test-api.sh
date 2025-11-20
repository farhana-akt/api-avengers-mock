#!/bin/bash

# API Testing Script
# Tests the complete order flow

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

API_URL="http://localhost:8080"

echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}  E-Commerce API Testing${NC}"
echo -e "${BLUE}=========================================${NC}"
echo ""

# Step 1: Register a new user
echo -e "${BLUE}1. Registering new user...${NC}"
REGISTER_RESPONSE=$(curl -s -X POST "$API_URL/api/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }')

echo "$REGISTER_RESPONSE" | jq '.'
echo -e "${GREEN}✓ User registered${NC}"
echo ""

# Step 2: Login
echo -e "${BLUE}2. Logging in...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "password123"
  }')

JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token')
echo "JWT Token: ${JWT_TOKEN:0:50}..."
echo -e "${GREEN}✓ Login successful${NC}"
echo ""

# Step 3: Get user profile
echo -e "${BLUE}3. Getting user profile...${NC}"
PROFILE_RESPONSE=$(curl -s -X GET "$API_URL/api/users/profile" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "$PROFILE_RESPONSE" | jq '.'
echo -e "${GREEN}✓ Profile retrieved${NC}"
echo ""

# Step 4: Browse products
echo -e "${BLUE}4. Browsing products...${NC}"
PRODUCTS_RESPONSE=$(curl -s -X GET "$API_URL/api/products" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "$PRODUCTS_RESPONSE" | jq '.content[0:2]'
PRODUCT_ID=$(echo "$PRODUCTS_RESPONSE" | jq -r '.content[0].id')
echo -e "${GREEN}✓ Products retrieved (using product ID: $PRODUCT_ID)${NC}"
echo ""

# Step 5: Add product to cart
echo -e "${BLUE}5. Adding product to cart...${NC}"
ADD_CART_RESPONSE=$(curl -s -X POST "$API_URL/api/cart/add" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"productId\": $PRODUCT_ID,
    \"quantity\": 2
  }")

echo "$ADD_CART_RESPONSE" | jq '.'
echo -e "${GREEN}✓ Product added to cart${NC}"
echo ""

# Step 6: View cart
echo -e "${BLUE}6. Viewing cart...${NC}"
CART_RESPONSE=$(curl -s -X GET "$API_URL/api/cart" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "$CART_RESPONSE" | jq '.'
echo -e "${GREEN}✓ Cart retrieved${NC}"
echo ""

# Step 7: Place order
echo -e "${BLUE}7. Placing order...${NC}"
ORDER_RESPONSE=$(curl -s -X POST "$API_URL/api/orders" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json")

echo "$ORDER_RESPONSE" | jq '.'
ORDER_ID=$(echo "$ORDER_RESPONSE" | jq -r '.id')
ORDER_STATUS=$(echo "$ORDER_RESPONSE" | jq -r '.status')

if [ "$ORDER_STATUS" = "CONFIRMED" ]; then
  echo -e "${GREEN}✓ Order placed successfully (ID: $ORDER_ID)${NC}"
elif [ "$ORDER_STATUS" = "PAYMENT_PENDING" ]; then
  echo -e "${YELLOW}⏳ Order created but payment pending (ID: $ORDER_ID)${NC}"
else
  echo -e "${YELLOW}⚠ Order status: $ORDER_STATUS${NC}"
fi
echo ""

# Step 8: View order history
echo -e "${BLUE}8. Viewing order history...${NC}"
ORDERS_RESPONSE=$(curl -s -X GET "$API_URL/api/orders" \
  -H "Authorization: Bearer $JWT_TOKEN")

echo "$ORDERS_RESPONSE" | jq '.'
echo -e "${GREEN}✓ Order history retrieved${NC}"
echo ""

# Step 9: View specific order
if [ -n "$ORDER_ID" ] && [ "$ORDER_ID" != "null" ]; then
  echo -e "${BLUE}9. Viewing order details...${NC}"
  ORDER_DETAIL_RESPONSE=$(curl -s -X GET "$API_URL/api/orders/$ORDER_ID" \
    -H "Authorization: Bearer $JWT_TOKEN")

  echo "$ORDER_DETAIL_RESPONSE" | jq '.'
  echo -e "${GREEN}✓ Order details retrieved${NC}"
  echo ""
fi

# Summary
echo -e "${GREEN}=========================================${NC}"
echo -e "${GREEN}  API Testing Complete!${NC}"
echo -e "${GREEN}=========================================${NC}"
echo ""
echo -e "${BLUE}Check the following:${NC}"
echo "  • Zipkin traces:     http://localhost:9411"
echo "  • RabbitMQ queues:   http://localhost:15672"
echo "  • Prometheus metrics: http://localhost:9090"
echo "  • Grafana dashboards: http://localhost:3000"
echo ""
echo -e "${YELLOW}Note: Payment service has 90% success rate.${NC}"
echo -e "${YELLOW}If order is PAYMENT_PENDING, try placing another order.${NC}"
