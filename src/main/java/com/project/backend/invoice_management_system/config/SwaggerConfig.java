package com.project.backend.invoice_management_system.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for SpringDoc OpenAPI (Swagger UI).
 */
@Configuration
public class SwaggerConfig {

    /**
     * This bean configures the main OpenAPI definition.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // This is just a name

        return new OpenAPI()
                // 1. Define the API info
                .info(new Info()
                        .title("Invoice Management System API")
                        .version("v1")
                        .description("API for the multi-tenant invoice platform."))

                // 2. Add a global security requirement
                // This adds the "lock" icon to all endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // 3. Define the components, including our security scheme
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP) // Type is HTTP
                                                .scheme("bearer") // Scheme is "bearer"
                                                .bearerFormat("JWT") // Format is "JWT"
                                                .description("Enter your Bearer Token")
                                )
                );
    }
}