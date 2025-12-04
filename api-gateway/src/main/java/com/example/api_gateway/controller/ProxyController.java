package com.example.api_gateway.controller;

import com.example.api_gateway.entity.EndpointEntity;
import com.example.api_gateway.service.EndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
public class ProxyController {

    private static final Logger log = LoggerFactory.getLogger(ProxyController.class);

    private final EndpointService endpointService;
    private final WebClient webClient;

    public ProxyController(EndpointService endpointService, WebClient webClient) {
        this.endpointService = endpointService;
        this.webClient = webClient;
    }

    @RequestMapping("/{service}/**")
    public Mono<ResponseEntity<byte[]>> proxy(@PathVariable("service") String service, ServerWebExchange exchange) {
        String rawPath = exchange.getRequest().getPath().pathWithinApplication().value();
        String prefix = "/" + service;
        String rest = rawPath.startsWith(prefix) ? rawPath.substring(prefix.length()) : "";
        if (rest.isEmpty()) rest = "/";

        HttpMethod method = exchange.getRequest().getMethod();
        String methodName = method != null ? method.name() : "GET";

        List<EndpointEntity> candidates = endpointService.findByServiceAndMethod(service, methodName);
        EndpointEntity matched = null;
        for (EndpointEntity e : candidates) {
            String epPath = e.getPath();
            if (!epPath.startsWith("/")) epPath = "/" + epPath;
            if (epPath.equals(rest)) {
                matched = e; break;
            }
        }

        if (matched == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(("No endpoint matched for path: " + rest).getBytes()));
        }

        // role check
        String requiredRole = matched.getRole();
        String rolesHeader = exchange.getRequest().getHeaders().getFirst("X-User-Roles");
        if (requiredRole != null && !requiredRole.isBlank()) {
            if (rolesHeader == null || !rolesHeader.contains(requiredRole)) {
                return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(("Forbidden: requires role " + requiredRole).getBytes()));
            }
        }

        String target = matched.getHost();
        String targetPath = matched.getPath();
        if (!targetPath.startsWith("/")) targetPath = "/" + targetPath;
        String uri = target.endsWith("/") ? target.substring(0, target.length()-1) + targetPath : target + targetPath;

        log.info("Proxying {} {} -> {}", methodName, rawPath, uri);

        WebClient.RequestBodySpec reqSpec = webClient.method(method == null ? HttpMethod.GET : method).uri(URI.create(uri)).headers(h -> {
            // copy selected headers
            exchange.getRequest().getHeaders().forEach((k, v) -> {
                if (!k.equalsIgnoreCase(HttpHeaders.HOST) && !k.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                    h.put(k, v);
                }
            });
        });

        Mono<ClientResponse> clientResponseMono;
        if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
            clientResponseMono = reqSpec.exchangeToMono(Mono::just);
        } else {
            clientResponseMono = reqSpec.body(BodyInserters.fromDataBuffers(exchange.getRequest().getBody())).exchangeToMono(Mono::just);
        }

        return clientResponseMono.flatMap(resp -> resp.bodyToMono(byte[].class).defaultIfEmpty(new byte[0]).map(body -> {
            HttpHeaders outHeaders = new HttpHeaders();
            resp.headers().asHttpHeaders().forEach((k, v) -> outHeaders.put(k, v));
            // remove transfer-encoding if present
            outHeaders.remove(HttpHeaders.TRANSFER_ENCODING);
            return new ResponseEntity<>(body, outHeaders, HttpStatus.valueOf(resp.statusCode().value()));
        })).onErrorResume(ex -> {
            log.error("Proxy error to {}: {}", uri, ex.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(("Bad Gateway: " + ex.getMessage()).getBytes()));
        });
    }
}

