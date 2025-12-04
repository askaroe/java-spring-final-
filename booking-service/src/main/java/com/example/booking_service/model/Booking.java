package com.example.booking_service.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public class Booking {
    private UUID id;
    private UUID userId;
    private UUID eventId;
    private String status; // PENDING, CONFIRMED
    private String paymentStatus;
    private BigDecimal totalAmount;
    private String currency;
    private List<BookingItem> items = new ArrayList<>();
    private Instant createdAt = Instant.now();

    public Booking() {}

    public Booking(UUID id, UUID userId, UUID eventId, List<BookingItem> items) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.items = items;
        this.status = "PENDING";
        this.paymentStatus = "NOT_PAID";
        this.currency = "KZT";
        this.totalAmount = items.stream().map(i -> i.getTotalPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public List<BookingItem> getItems() { return items; }
    public void setItems(List<BookingItem> items) { this.items = items; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

