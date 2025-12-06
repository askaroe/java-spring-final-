package com.example.api_gateway.controller;

import com.example.api_gateway.dto.ApiRouteDto;
import com.example.api_gateway.entity.ApiRoute;
import com.example.api_gateway.service.ApiRouteService;
import com.example.api_gateway.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
public class GatewayProxyController {

    private final ApiRouteService routeService;
    private final RestTemplate restTemplate = new RestTemplate();

    public GatewayProxyController(ApiRouteService routeService) {
        this.routeService = routeService;
    }

    // ---------- Main proxy ----------

    @RequestMapping("/api/**")
    public ResponseEntity<?> proxy(HttpServletRequest request) throws IOException {
        String method = request.getMethod();
        String fullPath = request.getRequestURI(); // e.g. /api/bookings

        // 1) Extract roles from JWT (Authorization header)
        String authHeader = request.getHeader("Authorization");
        Set<String> tokenRoles = JwtUtils.extractRealmRoles(authHeader);

        // 2) Find matching route in DB
        ApiRoute routeOpt = routeService.findMatchingRoute(method, fullPath);

//        ApiRoute route = routeOpt.get();

        // 3) Check requiredRole against token roles
        String requiredRole = routeOpt.getRequiredRole();
        if (requiredRole != null && !requiredRole.isBlank()) {
            boolean hasRole = tokenRoles.stream()
                    .anyMatch(r -> r.equalsIgnoreCase(requiredRole));
            if (!hasRole) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Forbidden: requires role " + requiredRole + ", token roles: " + tokenRoles);
            }
        }

        // 4) Build target URL
        String targetUrl = routeOpt.getTargetBaseUrl() + fullPath;

        // 5) Copy headers (except Host)
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if ("host".equalsIgnoreCase(name)) continue;
            headers.add(name, request.getHeader(name));
        }

        // 6) Copy body for non-GET/DELETE
        String body = null;
        if (!HttpMethod.GET.matches(method) && !HttpMethod.DELETE.matches(method)) {
            body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        }

        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        // 7) Forward to target service
        ResponseEntity<String> response = restTemplate.exchange(
                targetUrl,
                httpMethod,
                httpEntity,
                String.class
        );

        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }
}
