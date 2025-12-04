package com.example.api_gateway.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class JwtAuthHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .defaultIfEmpty(null)
                .flatMap(auth -> {
                    if (auth instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
                        Jwt jwt = jwtAuth.getToken();
                        String sub = jwt.getSubject();

                        // extract roles from common Keycloak claim locations
                        Collection<String> roles = extractRoles(jwt);
                        String rolesHeader = String.join(",", roles);

                        ServerWebExchange mutated = exchange.mutate()
                                .request(r -> r.header("X-User-Id", sub).header("X-User-Roles", rolesHeader))
                                .build();
                        return chain.filter(mutated);
                    }
                    return chain.filter(exchange);
                });
    }

    private Collection<String> extractRoles(Jwt jwt) {
        // 1) try realm_access.roles
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map) {
            Map map = (Map) realmAccess;
            Object rolesObj = map.get("roles");
            if (rolesObj instanceof Collection) {
                return ((Collection<?>) rolesObj).stream().map(Object::toString).collect(Collectors.toList());
            }
        }
        // 2) try resource_access.*.roles
        Object resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess instanceof Map) {
            Map ra = (Map) resourceAccess;
            for (Object key : ra.keySet()) {
                Object clientObj = ra.get(key);
                if (clientObj instanceof Map) {
                    Object rolesObj = ((Map) clientObj).get("roles");
                    if (rolesObj instanceof Collection) {
                        return ((Collection<?>) rolesObj).stream().map(Object::toString).collect(Collectors.toList());
                    }
                }
            }
        }
        // 3) try roles claim directly
        Object roles = jwt.getClaim("roles");
        if (roles instanceof Collection) {
            return ((Collection<?>) roles).stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}

