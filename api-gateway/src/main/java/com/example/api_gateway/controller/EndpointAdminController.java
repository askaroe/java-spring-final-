package com.example.api_gateway.controller;

import com.example.api_gateway.dto.EndpointRequest;
import com.example.api_gateway.entity.EndpointEntity;
import com.example.api_gateway.service.EndpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequestMapping("/api/gateway")
public class EndpointAdminController {

    private final EndpointService service;

    public EndpointAdminController(EndpointService service) {
        this.service = service;
    }

    @PostMapping("/endpoints")
    public ResponseEntity<?> createEndpoint(@RequestBody EndpointRequest req, ServerWebExchange exchange) {
        // simple role check using header injected by JwtAuthHeadersFilter
        String roles = exchange.getRequest().getHeaders().getFirst("X-User-Roles");
        if (roles == null || !roles.contains("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("Forbidden: requires ROLE_ADMIN");
        }

        EndpointEntity e = new EndpointEntity();
        e.setDescription(req.getDescription());
        e.setHost(req.getHost());
        e.setService(req.getService());
        e.setPath(req.getPath());
        e.setRole(req.getRole());
        e.setMethod(req.getMethod().toUpperCase());

        EndpointEntity saved = service.create(e);
        return ResponseEntity.ok(saved);
    }
}

