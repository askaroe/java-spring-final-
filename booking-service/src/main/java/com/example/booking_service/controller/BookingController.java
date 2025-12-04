package com.example.booking_service.controller;

import com.example.booking_service.dto.BookingDto;
import com.example.booking_service.dto.CreateBookingRequest;
import com.example.booking_service.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    @Value("${default.user.id}")
    private String defaultUserId;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody CreateBookingRequest req,
                                                    @RequestHeader(value = "X-User-Id", required = false) String userHeader) {
        UUID userId = userHeader != null ? UUID.fromString(userHeader) : UUID.fromString(defaultUserId);
        log.info("API createBooking called by user {} for event {}", userId, req.getEventId());
        BookingDto dto = bookingService.createBooking(req, userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable UUID id) {
        BookingDto dto = bookingService.getBooking(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingDto>> myBookings(@RequestHeader(value = "X-User-Id", required = false) String userHeader) {
        UUID userId = userHeader != null ? UUID.fromString(userHeader) : UUID.fromString(defaultUserId);
        List<BookingDto> list = bookingService.getByUser(userId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancel(@PathVariable UUID id) {
        BookingDto dto = bookingService.cancelBooking(id);
        return ResponseEntity.ok(dto);
    }
}
