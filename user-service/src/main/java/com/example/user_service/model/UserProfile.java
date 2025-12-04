package com.example.user_service.model;

import java.time.Instant;
import java.util.UUID;
import java.math.BigDecimal;

public class UserProfile {
    private UUID id;
    private String keycloakId;
    private String email;
    private String fullName;
    private String phone;
    private String status;
    private Instant createdAt;

    // new loyalty fields
    private int totalBookings = 0;
    private BigDecimal totalSpent = BigDecimal.ZERO;

    public UserProfile() {}

    public UserProfile(UUID id, String keycloakId, String email, String fullName) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.email = email;
        this.fullName = fullName;
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getKeycloakId() { return keycloakId; }
    public void setKeycloakId(String keycloakId) { this.keycloakId = keycloakId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public int getTotalBookings() { return totalBookings; }
    public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }
    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }
}
