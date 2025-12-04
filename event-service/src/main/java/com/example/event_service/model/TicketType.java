package com.example.event_service.model;

import java.math.BigDecimal;
import java.util.UUID;

public class TicketType {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String currency;
    private int totalQuantity;
    private int remainingQuantity;

    public TicketType() {}

    public TicketType(UUID id, String name, BigDecimal price, String currency, int totalQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = totalQuantity;
    }

    // getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public int getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(int remainingQuantity) { this.remainingQuantity = remainingQuantity; }
}

