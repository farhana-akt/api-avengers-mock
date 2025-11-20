package com.ecommerce.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Payment Service Application
 *
 * This service handles payment processing with a mock implementation (90% success rate).
 *
 * Key Features:
 * - Mock payment processing
 * - RabbitMQ event publishing for payment events
 * - Service discovery with Eureka
 * - Distributed tracing with Zipkin
 *
 * @author API Avengers Team - v1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
