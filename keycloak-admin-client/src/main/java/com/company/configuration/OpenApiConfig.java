package com.company.configuration;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Define the schema for the ErrorResponse
            Schema<?> errorResponseSchema = new Schema<>()
                    .properties(
                            Map.of(
                                    "timestamp", new Schema<LocalDateTime>().example("2024-08-27T08:20:50.051Z"),
                                    "status", new Schema<Integer>().example(400),
                                    "error", new Schema<String>().example("string")
                            )
                    );

            // Add the schema to the OpenAPI components
            openApi.getComponents().addSchemas("ErrorResponse", errorResponseSchema);

            // Define content for error responses
            Content content = new Content()
                    .addMediaType("application/json", new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")));

            // Update responses for each path
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                operation.getResponses().put("400", new ApiResponse().description("Bad Request").content(content));
                operation.getResponses().put("401", new ApiResponse().description("Unauthorized").content(content));
                operation.getResponses().put("403", new ApiResponse().description("Forbidden").content(content));
                operation.getResponses().put("404", new ApiResponse().description("Resource Not Found").content(content));
                operation.getResponses().put("409", new ApiResponse().description("Conflict").content(content));
                operation.getResponses().put("500", new ApiResponse().description("Internal Server Error").content(content));
            }));
        };
    }
}
