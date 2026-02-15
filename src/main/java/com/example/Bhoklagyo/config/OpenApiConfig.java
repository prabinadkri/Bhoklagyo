package com.example.Bhoklagyo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 / Swagger UI configuration.
 * Access the docs at /swagger-ui.html or /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Bhoklagyo}")
    private String applicationName;

    @Bean
    public OpenAPI bhoklagyoOpenAPI() {
        final String securitySchemeName = "Bearer JWT";

        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API")
                        .description("Food ordering and restaurant management platform API. "
                                + "Supports restaurant browsing, menu management, order processing, "
                                + "real-time WebSocket notifications, and admin operations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Bhoklagyo Team")
                                .email("support@bhoklagyo.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local development"),
                        new Server().url("http://bhoklagyo-app:8080").description("Docker environment")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token obtained from /auth/login or /admin/login")));
    }
}
