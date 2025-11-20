package com.ecommerce.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Notification Service Application
 *
 * This service listens to RabbitMQ events and sends notifications (email/SMS).
 *
 * Key Features:
 * - RabbitMQ message consumers for order and payment events
 * - Mock email notification service
 * - Asynchronous event processing
 * - Service discovery with Eureka
 * - Distributed tracing with Zipkin
 *
 * @version 1.0.0
 * @author API Avengers Team
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
