package com.example.booking_service.dto;

import java.util.List;
import java.util.UUID;

public class CreateBookingRequest {
    private UUID eventId;
    private List<Item> items;

    public static class Item {
        private UUID ticketTypeId;
        private int quantity;

        public UUID getTicketTypeId() { return ticketTypeId; }
        public void setTicketTypeId(UUID ticketTypeId) { this.ticketTypeId = ticketTypeId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}

