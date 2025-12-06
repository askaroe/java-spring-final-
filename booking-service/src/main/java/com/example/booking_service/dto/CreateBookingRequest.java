package com.example.booking_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class CreateBookingRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private UUID eventId;

    @NotNull
    private String currency;

    @NotEmpty
    private List<BookingItemDto> items;


    // ---------- Getters & Setters ----------

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<BookingItemDto> getItems() {
        return items;
    }

    public void setItems(List<BookingItemDto> items) {
        this.items = items;
    }
}
