package com.example.booking_service.service.impl;

import com.example.booking_service.dto.BookingItemDto;
import com.example.booking_service.dto.BookingResponse;
import com.example.booking_service.dto.CreateBookingRequest;
import com.example.booking_service.dto.UpdateBookingRequest;
import com.example.booking_service.entity.*;
import com.example.booking_service.exception.NotFoundException;
import com.example.booking_service.repository.BookingRepository;
import com.example.booking_service.service.BookingEventPublisher;
import com.example.booking_service.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingEventPublisher bookingEventPublisher;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingEventPublisher bookingEventPublisher) {
        this.bookingRepository = bookingRepository;
        this.bookingEventPublisher = bookingEventPublisher;
    }

    @Override
    public BookingResponse createBooking(CreateBookingRequest request) {
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setEventId(request.getEventId());
        booking.setCurrency(request.getCurrency());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.NOT_PAID);

        request.getItems().forEach(itemDto -> {
            BookingItem item = mapToEntityItem(itemDto);
            item.setBooking(booking);
            booking.getItems().add(item);
        });

        booking.setTotalAmount(calculateTotal(booking));

        Booking saved = bookingRepository.save(booking);

        // 👉 NEW: publish Kafka event
        bookingEventPublisher.publishBookingCreated(saved);

        return mapToResponse(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + id));
        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> listBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public BookingResponse updateBooking(UUID id, UpdateBookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + id));

        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
        }
        if (request.getPaymentStatus() != null) {
            booking.setPaymentStatus(request.getPaymentStatus());
        }
        if (request.getPaymentRef() != null) {
            booking.setPaymentRef(request.getPaymentRef());
        }

        if (request.getItems() != null) {
            booking.getItems().clear();
            request.getItems().forEach(itemDto -> {
                BookingItem item = mapToEntityItem(itemDto);
                item.setBooking(booking);
                booking.getItems().add(item);
            });
            booking.setTotalAmount(calculateTotal(booking));
        }

        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    @Override
    public void deleteBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + id));
        bookingRepository.delete(booking);
    }

    // helpers

    private BookingItem mapToEntityItem(BookingItemDto dto) {
        BookingItem item = new BookingItem();
        item.setTicketTypeId(dto.getTicketTypeId());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        // total_price will be auto-calculated in @PrePersist
        return item;
    }

    private BigDecimal calculateTotal(Booking booking) {
        return booking.getItems().stream()
                .map(BookingItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUserId());
        response.setEventId(booking.getEventId());
        response.setStatus(booking.getStatus());
        response.setTotalAmount(booking.getTotalAmount());
        response.setCurrency(booking.getCurrency());
        response.setPaymentStatus(booking.getPaymentStatus());
        response.setPaymentRef(booking.getPaymentRef());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());

        var items = booking.getItems().stream().map(item -> {
            BookingResponse.BookingItemResponse dto = new BookingResponse.BookingItemResponse();
            dto.setId(item.getId());
            dto.setTicketTypeId(item.getTicketTypeId());
            dto.setQuantity(item.getQuantity());
            dto.setUnitPrice(item.getUnitPrice());
            dto.setTotalPrice(item.getTotalPrice());
            return dto;
        }).collect(Collectors.toList());

        response.setItems(items);
        return response;
    }
}
