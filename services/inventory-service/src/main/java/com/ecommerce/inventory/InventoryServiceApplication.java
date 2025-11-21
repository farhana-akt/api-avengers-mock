package com.ecommerce.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Inventory Service Application
 *
 * This service manages product inventory and stock levels.
 *
 * Key Features:
 * - Stock level management
 * - Reserve/release stock operations for order processing
 * - Transaction support for inventory consistency
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
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
