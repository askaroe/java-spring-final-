package com.example.booking_service.dto;

import com.example.booking_service.entity.BookingStatus;
import com.example.booking_service.entity.PaymentStatus;

import java.util.List;

public class UpdateBookingRequest {

    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private String paymentRef;
    private List<BookingItemDto> items; // full replace when provided


    // ---------- Getters & Setters ----------

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public List<BookingItemDto> getItems() {
        return items;
    }

    public void setItems(List<BookingItemDto> items) {
        this.items = items;
    }
}
