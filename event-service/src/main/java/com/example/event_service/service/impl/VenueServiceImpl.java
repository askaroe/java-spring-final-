package com.example.event_service.service.impl;

import com.example.event_service.dto.VenueRequest;
import com.example.event_service.dto.VenueResponse;
import com.example.event_service.entity.Venue;
import com.example.event_service.exception.NotFoundException;
import com.example.event_service.repository.VenueRepository;
import com.example.event_service.service.VenueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    public VenueServiceImpl(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Override
    public VenueResponse create(VenueRequest request) {
        Venue venue = new Venue();
        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setCapacity(request.getCapacity());

        Venue saved = venueRepository.save(venue);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponse getById(UUID id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue not found: " + id));
        return mapToResponse(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VenueResponse> list(Pageable pageable) {
        return venueRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public VenueResponse update(UUID id, VenueRequest request) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue not found: " + id));

        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setCapacity(request.getCapacity());

        Venue saved = venueRepository.save(venue);
        return mapToResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue not found: " + id));
        venueRepository.delete(venue);
    }

    private VenueResponse mapToResponse(Venue venue) {
        VenueResponse res = new VenueResponse();
        res.setId(venue.getId());
        res.setName(venue.getName());
        res.setAddress(venue.getAddress());
        res.setCity(venue.getCity());
        res.setCapacity(venue.getCapacity());
        res.setCreatedAt(venue.getCreatedAt());
        res.setUpdatedAt(venue.getUpdatedAt());
        return res;
    }
}
