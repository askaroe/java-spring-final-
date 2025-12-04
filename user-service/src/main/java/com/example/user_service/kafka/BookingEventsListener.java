package com.example.user_service.kafka;

import com.example.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class BookingEventsListener {

    private static final Logger log = LoggerFactory.getLogger(BookingEventsListener.class);

    private final ObjectMapper objectMapper;
    private final UserService userService;

    public BookingEventsListener(ObjectMapper objectMapper, UserService userService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @KafkaListener(topics = "${booking.events.topic}")
    public void onMessage(String message) {
        try {
            Map<String, Object> envelope = objectMapper.readValue(message, Map.class);
            String eventType = (String) envelope.get("eventType");
            Object payloadObj = envelope.get("payload");

            Map<String, Object> payload;
            if (payloadObj instanceof Map) payload = (Map<String, Object>) payloadObj;
            else payload = objectMapper.convertValue(payloadObj, Map.class);

            log.info("Received event {} with payload={}", eventType, payload);

            switch (eventType) {
                case "BOOKING_CREATED":
                case "BOOKING_CONFIRMED":
                    handleBookingConfirmed(payload);
                    break;
                case "BOOKING_CANCELLED":
                    handleBookingCancelled(payload);
                    break;
                default:
                    log.info("Unhandled event type {}", eventType);
            }
        } catch (Exception ex) {
            log.error("Failed to process booking event: {}", ex.getMessage(), ex);
        }
    }

    private void handleBookingConfirmed(Map<String, Object> payload) {
        try {
            String userIdStr = (String) payload.get("userId");
            Object totalAmountObj = payload.get("totalAmount");
            BigDecimal amount = null;
            if (totalAmountObj != null) {
                amount = new BigDecimal(totalAmountObj.toString());
            }
            if (userIdStr != null) {
                UUID userId = UUID.fromString(userIdStr);
                userService.addBookingStats(userId, amount);
                log.info("Processed booking confirmed for user {} amount={}", userId, amount);
            } else if (payload.containsKey("keycloakId")) {
                String kc = (String) payload.get("keycloakId");
                userService.addBookingStatsByKeycloakId(kc, amount);
                log.info("Processed booking confirmed for keycloakId {} amount={}", kc, amount);
            } else {
                log.warn("Booking event has no user identifier: {}", payload);
            }
        } catch (Exception ex) {
            log.error("Error processing booking confirmed: {}", ex.getMessage(), ex);
        }
    }

    private void handleBookingCancelled(Map<String, Object> payload) {
        // For now we just log; could decrement counters or take other actions
        log.info("Booking cancelled payload={}", payload);
    }
}

