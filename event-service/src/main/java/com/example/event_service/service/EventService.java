package com.example.event_service.service;

import com.example.event_service.dto.EventRequest;
import com.example.event_service.dto.EventResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EventService {

    EventResponse create(EventRequest request);

    EventResponse getById(UUID id);

    Page<EventResponse> list(Pageable pageable);

    EventResponse update(UUID id, EventRequest request);

    void delete(UUID id);
}
