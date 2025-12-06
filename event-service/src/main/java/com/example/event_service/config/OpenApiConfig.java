package com.example.event_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Event Service API",
                version = "v1",
                description = "API for managing venues, events, and ticket types"
        )
)
public class OpenApiConfig {
}
