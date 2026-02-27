package com.mockbank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration
 * Provides API documentation and security scheme configuration
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mock Bank API")
                        .version("1.0.0")
                        .description("Mock Bank API for UPI Dispute Resolution Agent\n\n" +
                                "This API simulates real bank infrastructure for the UPI Dispute Resolution Agent.\n" +
                                "All requests require the x-api-key header for authentication.\n\n" +
                                "API Key: upi-dispute-resolver-secret-key-2024\n\n" +
                                "**Integration with Python Dispute Resolution Agent:**\n" +
                                "The Python agent calls these endpoints:\n" +
                                "- GET /bank/transaction/{transactionId} - To fetch transaction details\n" +
                                "- POST /bank/refund - To trigger refunds for failed transactions")
                        .contact(new Contact()
                                .name("UPI Dispute Resolution Team")
                                .email("support@upidispute.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("api_key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .name("x-api-key")
                                        .in(SecurityScheme.In.HEADER)
                                        .description("API Key for authentication")))
                .addSecurityItem(new SecurityRequirement().addList("api_key"));
    }
}
