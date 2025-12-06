package com.example.booking_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Booking Service API",
                version = "v1",
                description = "API for managing bookings, booking items, and outbox events"
        )
)
public class OpenApiConfig {
    // no code needed, annotations do the work
}
