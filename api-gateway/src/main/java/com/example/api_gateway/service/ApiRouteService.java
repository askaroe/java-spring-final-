package com.example.api_gateway.service;

import com.example.api_gateway.dto.ApiRouteDto;
import com.example.api_gateway.entity.ApiRoute;
import com.example.api_gateway.repository.ApiRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ApiRouteService {

    private final ApiRouteRepository routeRepository;

    public ApiRouteService(ApiRouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public ApiRouteDto create(ApiRouteDto dto) {
        ApiRoute route = new ApiRoute();
        route.setPathPrefix(dto.getPathPrefix());
        route.setHttpMethod(dto.getHttpMethod());
        route.setTargetBaseUrl(dto.getTargetBaseUrl());
        route.setRequiredRole(dto.getRequiredRole());

        ApiRoute saved = routeRepository.save(route);
        return toDto(saved);
    }

    public List<ApiRouteDto> list() {
        return routeRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public void delete(UUID id) {
        routeRepository.deleteById(id);
    }

    /**
     * Find the best matching route: same method (or "*"), and longest matching pathPrefix.
     */
    @Transactional(readOnly = true)
    public ApiRoute findMatchingRoute(String httpMethod, String fullPath) {
        String methodUpper = httpMethod.toUpperCase();

        List<ApiRoute> candidates = routeRepository
                .findByHttpMethodIgnoreCaseOrHttpMethod(methodUpper, "*");

        return candidates.stream()
                .filter(r -> fullPath.startsWith(r.getPathPrefix()))
                .max(Comparator.comparingInt(r -> r.getPathPrefix().length()))
                .orElse(null);
    }

    private ApiRouteDto toDto(ApiRoute route) {
        ApiRouteDto dto = new ApiRouteDto();
        dto.setId(route.getId());
        dto.setPathPrefix(route.getPathPrefix());
        dto.setHttpMethod(route.getHttpMethod());
        dto.setTargetBaseUrl(route.getTargetBaseUrl());
        dto.setRequiredRole(route.getRequiredRole());
        return dto;
    }

    public List<ApiRouteDto> findAll() {
        return routeRepository.findAll()
                .stream()
                .map(this::toDto)  // or whatever mapper you use
                .toList();
    }
}
