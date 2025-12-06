package com.example.booking_service.service;

import com.example.booking_service.dto.BookingResponse;
import com.example.booking_service.dto.CreateBookingRequest;
import com.example.booking_service.dto.UpdateBookingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BookingService {

    BookingResponse createBooking(CreateBookingRequest request);

    BookingResponse getBooking(UUID id);

    Page<BookingResponse> listBookings(Pageable pageable);

    BookingResponse updateBooking(UUID id, UpdateBookingRequest request);

    void deleteBooking(UUID id);
}
