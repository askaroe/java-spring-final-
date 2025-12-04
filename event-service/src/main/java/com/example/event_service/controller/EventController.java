package com.example.event_service.controller;

import com.example.event_service.model.Event;
import com.example.event_service.model.TicketType;
import com.example.event_service.model.ReserveRequest;
import com.example.event_service.model.ReserveRequest.ReserveItem;
import com.example.event_service.model.ReserveResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final Map<UUID, Event> events = Collections.synchronizedMap(new LinkedHashMap<>());

    @PostConstruct
    public void init() {
        // create sample event with two ticket types
        UUID eventId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Event e = new Event(eventId, "Sample Concert");
        TicketType t1 = new TicketType(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Standard", new BigDecimal("1000"), "KZT", 100);
        TicketType t2 = new TicketType(UUID.fromString("33333333-3333-3333-3333-333333333333"), "VIP", new BigDecimal("5000"), "KZT", 10);
        e.getTicketTypes().put(t1.getId(), t1);
        e.getTicketTypes().put(t2.getId(), t2);
        events.put(eventId, e);
    }

    @GetMapping
    public Collection<Event> list() {
        return events.values();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> get(@PathVariable UUID id) {
        Event e = events.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @GetMapping("/{id}/tickets")
    public ResponseEntity<Collection<TicketType>> getTickets(@PathVariable UUID id) {
        Event e = events.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e.getTicketTypes().values());
    }

    // Internal reservation endpoint used by booking service
    @PostMapping("/internal/{id}/reserve")
    public ResponseEntity<ReserveResponse> reserve(@PathVariable("id") UUID eventId, @RequestBody ReserveRequest request) {
        Event e = events.get(eventId);
        if (e == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ReserveResponse(false, "Event not found"));

        synchronized (e) {
            // check availability
            for (ReserveItem it : request.getItems()) {
                TicketType tt = e.getTicketTypes().get(it.getTicketTypeId());
                if (tt == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ReserveResponse(false, "Ticket type not found: " + it.getTicketTypeId()));
                if (tt.getRemainingQuantity() < it.getQuantity()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ReserveResponse(false, "Not enough tickets for " + tt.getName()));
                }
            }
            // reserve (decrement remaining quantity)
            for (ReserveItem it : request.getItems()) {
                TicketType tt = e.getTicketTypes().get(it.getTicketTypeId());
                tt.setRemainingQuantity(tt.getRemainingQuantity() - it.getQuantity());
            }
        }

        return ResponseEntity.ok(new ReserveResponse(true, "Reserved"));
    }

    @PostMapping("/internal/{id}/release")
    public ResponseEntity<ReserveResponse> release(@PathVariable("id") UUID eventId, @RequestBody ReserveRequest request) {
        Event e = events.get(eventId);
        if (e == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ReserveResponse(false, "Event not found"));

        synchronized (e) {
            for (ReserveItem it : request.getItems()) {
                TicketType tt = e.getTicketTypes().get(it.getTicketTypeId());
                if (tt == null) continue; // ignore unknown types on release
                tt.setRemainingQuantity(tt.getRemainingQuantity() + it.getQuantity());
            }
        }

        return ResponseEntity.ok(new ReserveResponse(true, "Released"));
    }
}

