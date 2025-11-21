package com.ecommerce.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Auth Service Application
 *
 * This service handles authentication for the e-commerce platform.
 *
 * Key Features:
 * - User registration with BCrypt password hashing
 * - User login with JWT token generation
 * - Shared database with user-service (userdb)
 * - Service discovery with Eureka
 * - Distributed tracing with Zipkin
 *
 * CI/CD Pipeline: Multi-stage build with unit tests, integration tests, and code quality checks
 *
 * @author API Avengers Team - v1.1
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
