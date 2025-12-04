package com.example.booking_service.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingDto {
    private UUID id;
    private UUID userId;
    private UUID eventId;
    private String status;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private String currency;
    private Instant createdAt;
    private List<Item> items = new ArrayList<>();

    public static class Item {
        private UUID id;
        private UUID ticketTypeId;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getTicketTypeId() { return ticketTypeId; }
        public void setTicketTypeId(UUID ticketTypeId) { this.ticketTypeId = ticketTypeId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }

    // getters & setters
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
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}

