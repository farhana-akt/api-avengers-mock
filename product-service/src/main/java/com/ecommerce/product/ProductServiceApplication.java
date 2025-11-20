package com.ecommerce.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Product Service Application
 *
 * This service manages the product catalog for the e-commerce platform.
 *
 * Key Features:
 * - Product CRUD operations
 * - Product search and filtering
 * - PostgreSQL database with Flyway migrations
 * - Service discovery with Eureka
 * - Distributed tracing with Zipkin
 *
 * @author API Avengers Team
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
