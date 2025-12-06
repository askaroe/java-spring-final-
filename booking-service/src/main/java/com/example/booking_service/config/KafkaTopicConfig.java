package com.example.booking_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic bookingEventsTopic() {
        // Topic name must match what you use in BookingEventPublisher (\"booking-events\")
        return TopicBuilder.name("booking-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
