package com.example.booking_service.model;

import java.math.BigDecimal;
import java.util.UUID;

public class BookingItem {
    private UUID id;
    private UUID ticketTypeId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public BookingItem() {}

    public BookingItem(UUID id, UUID ticketTypeId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.ticketTypeId = ticketTypeId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

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

