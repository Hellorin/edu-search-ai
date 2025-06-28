package io.hellorin.edusearchai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI 3 (Swagger) documentation.
 * This class customizes the API documentation for the EduSearchAI application.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EduSearchAI API")
                        .description("REST API for educational document search and AI-powered question answering. " +
                                   "This API allows users to search through educational documents and get AI-generated " +
                                   "answers to their questions based on the document content.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hellorin")
                                .url("https://github.com/hellorin/eduSearchAi"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server"),
                        new Server()
                                .url("https://api.edusearchai.com")
                                .description("Production server")
                ));
    }
} 