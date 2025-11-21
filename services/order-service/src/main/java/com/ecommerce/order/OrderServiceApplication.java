package com.ecommerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Order Service Application
 *
 * This service orchestrates the order process, coordinating between Cart, Product, Inventory, and Payment services.
 *
 * Key Features:
 * - Order creation and management
 * - OpenFeign clients for inter-service communication
 * - Circuit Breaker with Resilience4j for fault tolerance
 * - RabbitMQ event publishing for order events
 * - Compensating transactions for failure scenarios
 * - PostgreSQL database with Flyway migrations
 * - Service discovery with Eureka
 * - Distributed tracing with Zipkin
 *
 * CI/CD Pipeline: Multi-stage build with unit tests, integration tests, and code quality checks
 *
 * @author API Avengers Team - v1.1
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
