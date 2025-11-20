package com.ecommerce.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server - Centralized Configuration Management
 *
 * This server provides centralized external configuration for all microservices.
 * Configurations are stored in a native file system (can be Git-backed in production).
 *
 * Port: 8888
 * Access: http://localhost:8888/{application}/{profile}
 * @author API Avengers Team - v1.0
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
