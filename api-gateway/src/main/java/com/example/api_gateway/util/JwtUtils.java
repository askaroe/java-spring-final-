package com.example.api_gateway.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class JwtUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extract realm roles from a Keycloak JWT without verifying the signature.
     * Looks at claim: realm_access.roles
     */
    public static Set<String> extractRealmRoles(String jwtToken) {
        try {
            if (jwtToken == null || jwtToken.isBlank()) {
                return Collections.emptySet();
            }

            // Remove "Bearer " prefix if present
            if (jwtToken.toLowerCase().startsWith("bearer ")) {
                jwtToken = jwtToken.substring(7);
            }

            String[] parts = jwtToken.split("\\.");
            if (parts.length != 3) {
                return Collections.emptySet();
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            JsonNode root = objectMapper.readTree(payloadJson);
            JsonNode realmAccess = root.get("realm_access");
            if (realmAccess == null || realmAccess.isMissingNode()) {
                return Collections.emptySet();
            }

            JsonNode rolesNode = realmAccess.get("roles");
            if (rolesNode == null || !rolesNode.isArray()) {
                return Collections.emptySet();
            }

            Set<String> roles = new HashSet<>();
            for (JsonNode r : rolesNode) {
                if (r.isTextual()) {
                    roles.add(r.asText());
                }
            }
            return roles;
        } catch (Exception e) {
            // Log in real life, but for now just return empty
            return Collections.emptySet();
        }
    }
}
