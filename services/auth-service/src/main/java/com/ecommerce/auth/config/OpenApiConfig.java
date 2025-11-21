package com.ecommerce.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration for Auth Service
 * Provides API documentation at /swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Auth Service API")
                .version("1.0")
                .description("Authentication service with JWT token generation for user registration and login")
                .contact(new Contact()
                    .name("E-Commerce Platform Team")
                    .email("team@ecommerce.com")));
    }
}
