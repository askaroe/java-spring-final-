package com.example.booking_service.service;

import com.example.booking_service.dto.CreateBookingRequest;
import com.example.booking_service.dto.BookingDto;
import com.example.booking_service.entity.BookingEntity;
import com.example.booking_service.entity.BookingItemEntity;
import com.example.booking_service.entity.OutboxEventEntity;
import com.example.booking_service.repository.BookingRepository;
import com.example.booking_service.repository.OutboxRepository;
import com.example.booking_service.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final OutboxRepository outboxRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${event.service.url}")
    private String eventServiceUrl;

    @Value("${booking.events.topic}")
    private String bookingEventsTopic;

    public BookingService(BookingRepository bookingRepository, OutboxRepository outboxRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.bookingRepository = bookingRepository;
        this.outboxRepository = outboxRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public BookingDto createBooking(CreateBookingRequest req, UUID userId) {
        UUID bookingId = UUID.randomUUID();

        // Call event service to reserve tickets
        Map<String, Object> reserveReq = new HashMap<>();
        reserveReq.put("bookingId", bookingId.toString());
        List<Map<String, Object>> itemsReq = req.getItems().stream().map(it -> Map.of(
                "ticketTypeId", it.getTicketTypeId(),
                "quantity", it.getQuantity()
        )).collect(Collectors.toList());
        reserveReq.put("items", itemsReq);

        String url = eventServiceUrl + "/api/events/internal/" + req.getEventId() + "/reserve";
        log.info("Calling event-service reserve at {} for booking {}", url, bookingId);
        restTemplate.postForEntity(url, reserveReq, Map.class);

        // create entities
        BookingEntity be = new BookingEntity();
        be.setId(bookingId);
        be.setUserId(userId);
        be.setEventId(req.getEventId());
        be.setStatus("PENDING");
        be.setPaymentStatus("NOT_PAID");
        be.setCurrency("KZT");

        List<BookingItemEntity> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CreateBookingRequest.Item it : req.getItems()) {
            BookingItemEntity ie = new BookingItemEntity();
            ie.setId(UUID.randomUUID());
            ie.setBooking(be);
            ie.setTicketTypeId(it.getTicketTypeId());
            ie.setQuantity(it.getQuantity());
            ie.setUnitPrice(BigDecimal.ZERO);
            ie.setTotalPrice(BigDecimal.ZERO);
            items.add(ie);
            total = total.add(ie.getTotalPrice());
        }
        be.setItems(items);
        be.setTotalAmount(total);

        bookingRepository.save(be);
        log.info("Booking {} persisted", bookingId);

        // create outbox event for booking created
        try {
            OutboxEventEntity out = new OutboxEventEntity();
            out.setId(UUID.randomUUID());
            out.setAggregateType("BOOKING");
            out.setAggregateId(bookingId);
            out.setEventType("BOOKING_CREATED");
            Map<String, Object> payload = new HashMap<>();
            payload.put("bookingId", bookingId.toString());
            payload.put("userId", userId.toString());
            payload.put("eventId", req.getEventId().toString());
            payload.put("totalAmount", total);
            payload.put("currency", be.getCurrency());
            payload.put("status", be.getStatus());
            out.setPayload(objectMapper.writeValueAsString(payload));
            out.setStatus("NEW");
            out.setCreatedAt(Instant.now());
            outboxRepository.save(out);
            log.info("Outbox event {} created for booking {}", out.getId(), bookingId);
        } catch (Exception ex) {
            log.error("Failed to create outbox event for booking {}: {}", bookingId, ex.getMessage());
        }

        return toDto(be);
    }

    @Transactional(readOnly = true)
    public BookingDto getBooking(UUID id) {
        BookingEntity be = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
        return toDto(be);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getByUser(UUID userId) {
        List<BookingEntity> list = bookingRepository.findByUserId(userId);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public BookingDto cancelBooking(UUID id) {
        BookingEntity be = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
        if ("CANCELLED".equals(be.getStatus())) return toDto(be);

        // call event-service release
        Map<String, Object> releaseReq = new HashMap<>();
        releaseReq.put("bookingId", be.getId().toString());
        List<Map<String, Object>> itemsReq = be.getItems().stream().map(i -> Map.of(
                "ticketTypeId", i.getTicketTypeId(),
                "quantity", i.getQuantity()
        )).collect(Collectors.toList());
        releaseReq.put("items", itemsReq);

        String url = eventServiceUrl + "/api/events/internal/" + be.getEventId() + "/release";
        try {
            restTemplate.postForEntity(url, releaseReq, Map.class);
        } catch (Exception ex) {
            log.error("Release call failed for booking {}: {}", be.getId(), ex.getMessage());
        }

        be.setStatus("CANCELLED");
        bookingRepository.save(be);

        // create outbox event BOOKING_CANCELLED
        try {
            OutboxEventEntity out = new OutboxEventEntity();
            out.setId(UUID.randomUUID());
            out.setAggregateType("BOOKING");
            out.setAggregateId(be.getId());
            out.setEventType("BOOKING_CANCELLED");
            Map<String, Object> payload = new HashMap<>();
            payload.put("bookingId", be.getId().toString());
            payload.put("userId", be.getUserId().toString());
            payload.put("eventId", be.getEventId().toString());
            payload.put("status", be.getStatus());
            out.setPayload(objectMapper.writeValueAsString(payload));
            out.setStatus("NEW");
            out.setCreatedAt(Instant.now());
            outboxRepository.save(out);
            log.info("Outbox event {} created for booking cancellation {}", out.getId(), be.getId());
        } catch (Exception ex) {
            log.error("Failed to create outbox event for cancellation {}: {}", be.getId(), ex.getMessage());
        }

        return toDto(be);
    }

    private BookingDto toDto(BookingEntity be) {
        BookingDto dto = new BookingDto();
        dto.setId(be.getId());
        dto.setUserId(be.getUserId());
        dto.setEventId(be.getEventId());
        dto.setStatus(be.getStatus());
        dto.setPaymentStatus(be.getPaymentStatus());
        dto.setTotalAmount(be.getTotalAmount());
        dto.setCurrency(be.getCurrency());
        dto.setCreatedAt(be.getCreatedAt());
        dto.setItems(be.getItems().stream().map(i -> {
            BookingDto.Item it = new BookingDto.Item();
            it.setId(i.getId());
            it.setTicketTypeId(i.getTicketTypeId());
            it.setQuantity(i.getQuantity());
            it.setUnitPrice(i.getUnitPrice());
            it.setTotalPrice(i.getTotalPrice());
            return it;
        }).collect(Collectors.toList()));
        return dto;
    }
}
