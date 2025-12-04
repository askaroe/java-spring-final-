package com.example.user_service.service;

import com.example.user_service.model.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final Map<String, UserProfile> byKeycloakId = new ConcurrentHashMap<>();
    private final Map<UUID, UserProfile> byId = new ConcurrentHashMap<>();

    public UserProfile getOrCreateByKeycloakId(String keycloakId) {
        return byKeycloakId.computeIfAbsent(keycloakId, kc -> {
            UUID id = UUID.randomUUID();
            UserProfile p = new UserProfile(id, kc, null, "");
            byId.put(id, p);
            log.info("Created new user profile {} for keycloakId={}", id, kc);
            return p;
        });
    }

    public UserProfile getById(UUID id) {
        return byId.get(id);
    }

    public UserProfile getOrCreateById(UUID id) {
        UserProfile p = byId.get(id);
        if (p == null) {
            // create placeholder with id as internal id and empty keycloakId
            p = new UserProfile(id, null, null, "");
            byId.put(id, p);
            log.info("Created placeholder user profile {}", id);
        }
        return p;
    }

    public void updateProfile(UUID id, String fullName, String email, String phone) {
        UserProfile p = getOrCreateById(id);
        if (fullName != null) p.setFullName(fullName);
        if (email != null) p.setEmail(email);
        if (phone != null) p.setPhone(phone);
    }

    public void addBookingStats(UUID id, BigDecimal amount) {
        UserProfile p = getOrCreateById(id);
        p.setTotalBookings(p.getTotalBookings() + 1);
        if (amount != null) p.setTotalSpent(p.getTotalSpent().add(amount));
        log.info("Updated stats for user {}: totalBookings={}, totalSpent={}", id, p.getTotalBookings(), p.getTotalSpent());
    }

    public void addBookingStatsByKeycloakId(String keycloakId, BigDecimal amount) {
        UserProfile p = getOrCreateByKeycloakId(keycloakId);
        p.setTotalBookings(p.getTotalBookings() + 1);
        if (amount != null) p.setTotalSpent(p.getTotalSpent().add(amount));
        log.info("Updated stats for keycloakId {}: totalBookings={}, totalSpent={}", keycloakId, p.getTotalBookings(), p.getTotalSpent());
    }
}

