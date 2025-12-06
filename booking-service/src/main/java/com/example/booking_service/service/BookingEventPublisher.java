package com.example.booking_service.service;

import com.example.booking_service.entity.Booking;
import com.example.booking_service.event.BookingCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BookingEventPublisher.class);
    private static final String TOPIC = "booking-events";

    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

    public BookingEventPublisher(KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBookingCreated(Booking booking) {
        BookingCreatedEvent event = new BookingCreatedEvent();
        event.setBookingId(booking.getId());
        event.setEventId(booking.getEventId());
        event.setUserId(booking.getUserId());
        event.setTotalAmount(booking.getTotalAmount());
        event.setCurrency(booking.getCurrency());
        event.setStatus(booking.getStatus().name());

        log.info("Publishing BookingCreatedEvent to Kafka: bookingId={}", booking.getId());
        kafkaTemplate.send(TOPIC, booking.getId().toString(), event);
    }
}
