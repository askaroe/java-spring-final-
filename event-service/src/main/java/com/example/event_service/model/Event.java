package com.example.event_service.model;

import java.time.LocalDateTime;
import java.util.*;

public class Event {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UUID venueId;
    private String status;
    private Map<UUID, TicketType> ticketTypes = new LinkedHashMap<>();

    public Event() {}

    public Event(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    // getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public UUID getVenueId() { return venueId; }
    public void setVenueId(UUID venueId) { this.venueId = venueId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Map<UUID, TicketType> getTicketTypes() { return ticketTypes; }
    public void setTicketTypes(Map<UUID, TicketType> ticketTypes) { this.ticketTypes = ticketTypes; }
}

