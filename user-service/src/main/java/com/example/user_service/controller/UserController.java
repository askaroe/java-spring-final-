package com.example.user_service.controller;

import com.example.user_service.model.UserProfile;
import com.example.user_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Value("${default.user.keycloak-id}")
    private String defaultKeycloakId;

    @Value("${user.events.topic:user.events}")
    private String userEventsTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(UserService userService, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> me(@RequestHeader(value = "X-User-Id", required = false) String userHeader) {
        String keycloakId = userHeader != null ? userHeader : defaultKeycloakId;
        UserProfile p = userService.getOrCreateByKeycloakId(keycloakId);
        // If newly created, the service logs creation; publish USER_CREATED event
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", p.getId().toString());
            payload.put("keycloakId", p.getKeycloakId());
            payload.put("email", p.getEmail());
            payload.put("fullName", p.getFullName());
            String envelope = objectMapper.writeValueAsString(Map.of("eventType", "USER_CREATED", "payload", payload));
            kafkaTemplate.send(userEventsTopic, p.getId().toString(), envelope).addCallback(
                    result -> log.info("Published USER_CREATED for user {}", p.getId()),
                    ex -> log.error("Failed to publish USER_CREATED for user {}: {}", p.getId(), ex.getMessage())
            );
        } catch (Exception ex) {
            log.error("Failed to publish USER_CREATED event: {}", ex.getMessage());
        }

        return ResponseEntity.ok(p);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfile> updateMe(@RequestHeader(value = "X-User-Id", required = false) String userHeader,
                                                @RequestBody Map<String, String> body) {
        String keycloakId = userHeader != null ? userHeader : defaultKeycloakId;
        UserProfile p = userService.getOrCreateByKeycloakId(keycloakId);
        if (body.containsKey("fullName")) p.setFullName(body.get("fullName"));
        if (body.containsKey("phone")) p.setPhone(body.get("phone"));
        if (body.containsKey("email")) p.setEmail(body.get("email"));
        log.info("Updated user profile {} (keycloakId={})", p.getId(), keycloakId);
        return ResponseEntity.ok(p);
    }

    // internal endpoint for other services
    @GetMapping("/internal/{id}")
    public ResponseEntity<UserProfile> byId(@PathVariable UUID id) {
        UserProfile p = userService.getById(id);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }
}
