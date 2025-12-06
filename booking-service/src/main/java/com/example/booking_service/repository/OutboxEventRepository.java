package com.example.booking_service.repository;

import com.example.booking_service.entity.OutboxEvent;
import com.example.booking_service.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatusAndCreatedAtBefore(OutboxStatus status, LocalDateTime before);
}
