package com.example.booking_service.service;

import com.example.booking_service.entity.OutboxEventEntity;
import com.example.booking_service.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${booking.events.topic}")
    private String bookingEventsTopic;

    public OutboxPublisher(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-ms:5000}")
    public void pollAndPublish() {
        List<OutboxEventEntity> events = outboxRepository.findByStatusOrderByCreatedAtAsc("NEW");
        for (OutboxEventEntity e : events) {
            try {
                // build envelope with eventType and payload
                String envelope = objectMapper.writeValueAsString(
                        java.util.Map.of("eventType", e.getEventType(), "payload", objectMapper.readValue(e.getPayload(), Object.class))
                );
                // publish to kafka synchronously
                kafkaTemplate.send(bookingEventsTopic, e.getAggregateId().toString(), envelope).get();

                // mark published
                markPublished(e);
                log.info("Published outbox event {} to topic {}", e.getId(), bookingEventsTopic);
            } catch (Exception ex) {
                log.error("Failed to publish outbox event {}: {}", e.getId(), ex.getMessage());
                try {
                    e.setStatus("FAILED");
                    e.setProcessedAt(Instant.now());
                    outboxRepository.save(e);
                } catch (Exception ignore) {}
            }
        }
    }

    @Transactional
    protected void markPublished(OutboxEventEntity e) {
        e.setStatus("PUBLISHED");
        e.setProcessedAt(Instant.now());
        outboxRepository.save(e);
    }
}

