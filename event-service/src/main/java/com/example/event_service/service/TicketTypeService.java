package com.example.event_service.service;

import com.example.event_service.dto.TicketTypeRequest;
import com.example.event_service.dto.TicketTypeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TicketTypeService {

    TicketTypeResponse getById(UUID id);

    Page<TicketTypeResponse> list(Pageable pageable);

    // optional: create/update/delete separately if needed later
}
