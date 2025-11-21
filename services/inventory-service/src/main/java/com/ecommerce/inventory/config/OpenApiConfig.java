package com.ecommerce.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration for Inventory Service
 * Provides API documentation at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Inventory Service API")
                .version("1.0")
                .description("Inventory management service for stock tracking and reservation")
                .contact(new Contact()
                    .name("E-Commerce Platform Team")
                    .email("team@ecommerce.com")));
    }
}
