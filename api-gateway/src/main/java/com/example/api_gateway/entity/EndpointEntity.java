package com.example.api_gateway.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "endpoints")
public class EndpointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private String host;
    private String service;
    private String path;
    private String role;
    private String method;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public EndpointEntity() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

