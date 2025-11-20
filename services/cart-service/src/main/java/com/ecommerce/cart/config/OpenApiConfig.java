package com.ecommerce.cart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration for Cart Service
 * Provides API documentation at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cartServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Cart Service API")
                .version("1.0")
                .description("Shopping cart service with Redis-based session management")
                .contact(new Contact()
                    .name("E-Commerce Platform Team")
                    .email("team@ecommerce.com")));
    }
}
