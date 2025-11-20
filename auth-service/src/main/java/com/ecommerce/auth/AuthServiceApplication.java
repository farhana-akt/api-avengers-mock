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
 * @author API Avengers Team - v1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
