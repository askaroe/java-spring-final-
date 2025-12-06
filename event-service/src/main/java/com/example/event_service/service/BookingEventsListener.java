package com.example.event_service.service;

import com.example.event_service.event.BookingCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingEventsListener {

    private static final Logger log = LoggerFactory.getLogger(BookingEventsListener.class);

    @KafkaListener(
            topics = "booking-events",
            groupId = "event-service",
            containerFactory = "bookingKafkaListenerContainerFactory"
    )
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.info("Received BookingCreatedEvent from Kafka: bookingId={}, eventId={}, userId={}, totalAmount={} {} status={}",
                event.getBookingId(),
                event.getEventId(),
                event.getUserId(),
                event.getTotalAmount(),
                event.getCurrency(),
                event.getStatus());
    }
}
