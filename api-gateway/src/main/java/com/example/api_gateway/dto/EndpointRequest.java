package com.example.api_gateway.dto;

public class EndpointRequest {
    private String description;
    private String host;
    private String service;
    private String path;
    private String role;
    private String method;

    public EndpointRequest() {}

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
}

