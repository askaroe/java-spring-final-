package com.example.api_gateway.controller;

import com.example.api_gateway.dto.ApiRouteDto;
import com.example.api_gateway.service.ApiRouteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/routes")
public class ApiRouteController {

    private final ApiRouteService routeService;

    public ApiRouteController(ApiRouteService routeService) {
        this.routeService = routeService;
    }

    // Only ADMIN can manage routes
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiRouteDto create(@RequestBody ApiRouteDto dto) {
        return routeService.create(dto);
    }

    @GetMapping
    public List<ApiRouteDto> list() {
        return routeService.list();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        routeService.delete(id);
    }
}
