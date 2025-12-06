package com.example.event_service.service.impl;

import com.example.event_service.dto.EventRequest;
import com.example.event_service.dto.EventResponse;
import com.example.event_service.dto.TicketTypeRequest;
import com.example.event_service.dto.TicketTypeResponse;
import com.example.event_service.entity.*;
import com.example.event_service.exception.NotFoundException;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.repository.TicketTypeRepository;
import com.example.event_service.repository.VenueRepository;
import com.example.event_service.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final TicketTypeRepository ticketTypeRepository;

    public EventServiceImpl(EventRepository eventRepository,
                            VenueRepository venueRepository,
                            TicketTypeRepository ticketTypeRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.ticketTypeRepository = ticketTypeRepository;
    }

    @Override
    public EventResponse create(EventRequest request) {
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new NotFoundException("Venue not found: " + request.getVenueId()));

        Event event = new Event();
        event.setOrganizerId(request.getOrganizerId());
        event.setVenue(venue);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setStatus(request.getStatus() != null ? request.getStatus() : EventStatus.DRAFT);

        if (request.getTicketTypes() != null) {
            for (TicketTypeRequest ttReq : request.getTicketTypes()) {
                TicketType tt = new TicketType();
                tt.setEvent(event);
                tt.setName(ttReq.getName());
                tt.setPrice(ttReq.getPrice());
                tt.setCurrency(ttReq.getCurrency());
                tt.setTotalQuantity(ttReq.getTotalQuantity());
                // remainingQuantity will be set in @PrePersist if null
                event.getTicketTypes().add(tt);
            }
        }

        Event saved = eventRepository.save(event);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getById(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));
        return mapToResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> list(Pageable pageable) {
        return eventRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public EventResponse update(UUID id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));

        if (request.getOrganizerId() != null) {
            event.setOrganizerId(request.getOrganizerId());
        }

        if (request.getVenueId() != null) {
            Venue venue = venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new NotFoundException("Venue not found: " + request.getVenueId()));
            event.setVenue(venue);
        }

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }

        if (request.getCategory() != null) {
            event.setCategory(request.getCategory());
        }

        if (request.getStartTime() != null) {
            event.setStartTime(request.getStartTime());
        }

        if (request.getEndTime() != null) {
            event.setEndTime(request.getEndTime());
        }

        if (request.getStatus() != null) {
            event.setStatus(request.getStatus());
        }

        if (request.getTicketTypes() != null) {
            // full replace ticket types
            ticketTypeRepository.deleteAll(event.getTicketTypes());
            event.getTicketTypes().clear();

            for (TicketTypeRequest ttReq : request.getTicketTypes()) {
                TicketType tt = new TicketType();
                tt.setEvent(event);
                tt.setName(ttReq.getName());
                tt.setPrice(ttReq.getPrice());
                tt.setCurrency(ttReq.getCurrency());
                tt.setTotalQuantity(ttReq.getTotalQuantity());
                event.getTicketTypes().add(tt);
            }
        }

        Event saved = eventRepository.save(event);
        return mapToResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));
        eventRepository.delete(event);
    }

    private EventResponse mapToResponse(Event event) {
        EventResponse res = new EventResponse();
        res.setId(event.getId());
        res.setOrganizerId(event.getOrganizerId());
        res.setVenueId(event.getVenue().getId());
        res.setTitle(event.getTitle());
        res.setDescription(event.getDescription());
        res.setCategory(event.getCategory());
        res.setStartTime(event.getStartTime());
        res.setEndTime(event.getEndTime());
        res.setStatus(event.getStatus());
        res.setCreatedAt(event.getCreatedAt());
        res.setUpdatedAt(event.getUpdatedAt());

        var ticketDtos = event.getTicketTypes().stream()
                .map(tt -> {
                    TicketTypeResponse dto = new TicketTypeResponse();
                    dto.setId(tt.getId());
                    dto.setEventId(event.getId());
                    dto.setName(tt.getName());
                    dto.setPrice(tt.getPrice());
                    dto.setCurrency(tt.getCurrency());
                    dto.setTotalQuantity(tt.getTotalQuantity());
                    dto.setRemainingQuantity(tt.getRemainingQuantity());
                    dto.setCreatedAt(tt.getCreatedAt());
                    dto.setUpdatedAt(tt.getUpdatedAt());
                    return dto;
                }).collect(Collectors.toList());

        res.setTicketTypes(ticketDtos);
        return res;
    }
}
