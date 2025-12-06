package com.example.api_gateway.controller;

import com.example.api_gateway.entity.ApiRoute;
import com.example.api_gateway.service.ApiRouteService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GatewayController {

    private static final Logger log = LoggerFactory.getLogger(GatewayController.class);

    private final ApiRouteService routeService;
    private final RestTemplate restTemplate = new RestTemplate();

    public GatewayController(ApiRouteService routeService) {
        this.routeService = routeService;
    }

    @RequestMapping("/**")
    public ResponseEntity<?> proxy(HttpServletRequest request,
                                   @RequestBody(required = false) byte[] body,
                                   Authentication authentication) throws IOException {

        String method = request.getMethod();
        String fullPath = request.getRequestURI(); // e.g. /api/bookings/123

        log.info("Gateway received {} {}", method, fullPath);

        ApiRoute route = routeService.findMatchingRoute(method, fullPath);
        if (route == null) {
            log.warn("No route found for {} {}", method, fullPath);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No route configured for this path");
        }

        // Role check
        if (route.getRequiredRole() != null && !route.getRequiredRole().isBlank()) {
            String needed = "ROLE_" + route.getRequiredRole().toUpperCase();
            boolean hasRole = authentication != null &&
                    authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .anyMatch(a -> a.equals(needed));

            if (!hasRole) {
                log.warn("Access denied: requiredRole={} for path={}, userAuthorities={}",
                        needed, fullPath,
                        authentication != null ? authentication.getAuthorities() : List.of());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Forbidden: insufficient role");
            }
        }

        // Build target URL
        String pathWithoutApi = fullPath.substring("/api".length()); // /bookings/123
        String targetUrl = route.getTargetBaseUrl() + route.getPathPrefix();

        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isBlank()) {
            targetUrl = targetUrl + "?" + queryString;
        }

        log.info("Forwarding {} {} to {}", method, fullPath, targetUrl);

        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body("Unsupported HTTP method: " + method);
        }

        HttpHeaders headers = extractHeaders(request);
        // You may want to remove Host header
        headers.remove(HttpHeaders.HOST);

        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                targetUrl,
                httpMethod,
                entity,
                byte[].class
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

    private HttpHeaders extractHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            List<String> values = Collections.list(request.getHeaders(name));
            headers.put(name, values);
        }
        return headers;
    }
}
