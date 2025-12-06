package com.example.event_service.service;

import com.example.event_service.dto.VenueRequest;
import com.example.event_service.dto.VenueResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VenueService {

    VenueResponse create(VenueRequest request);

    VenueResponse getById(UUID id);

    Page<VenueResponse> list(Pageable pageable);

    VenueResponse update(UUID id, VenueRequest request);

    void delete(UUID id);
}
