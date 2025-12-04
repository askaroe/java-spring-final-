package com.example.event_service.model;

import java.util.List;
import java.util.UUID;

public class ReserveRequest {
    private UUID bookingId;
    private List<ReserveItem> items;

    public ReserveRequest() {}

    public ReserveRequest(UUID bookingId, List<ReserveItem> items) {
        this.bookingId = bookingId;
        this.items = items;
    }

    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
    public List<ReserveItem> getItems() { return items; }
    public void setItems(List<ReserveItem> items) { this.items = items; }

    public static class ReserveItem {
        private UUID ticketTypeId;
        private int quantity;

        public ReserveItem() {}
        public ReserveItem(UUID ticketTypeId, int quantity) {
            this.ticketTypeId = ticketTypeId;
            this.quantity = quantity;
        }

        public UUID getTicketTypeId() { return ticketTypeId; }
        public void setTicketTypeId(UUID ticketTypeId) { this.ticketTypeId = ticketTypeId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}

