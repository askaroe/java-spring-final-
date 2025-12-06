package com.example.event_service.controller;

import com.example.event_service.dto.VenueRequest;
import com.example.event_service.dto.VenueResponse;
import com.example.event_service.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

import java.util.UUID;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse create(@Valid @RequestBody VenueRequest request) {
        return venueService.create(request);
    }

    @GetMapping("/{id}")
    public VenueResponse getById(@PathVariable UUID id) {
        return venueService.getById(id);
    }

    @GetMapping
    public Page<VenueResponse> list(@ParameterObject Pageable pageable) {
        return venueService.list(pageable);
    }


    @PutMapping("/{id}")
    public VenueResponse update(@PathVariable UUID id,
                                @Valid @RequestBody VenueRequest request) {
        return venueService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        venueService.delete(id);
    }
}
