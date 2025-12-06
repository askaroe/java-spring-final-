package com.example.api_gateway.repository;

import com.example.api_gateway.entity.ApiRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApiRouteRepository extends JpaRepository<ApiRoute, UUID> {

    List<ApiRoute> findByHttpMethodIgnoreCase(String httpMethod);

    List<ApiRoute> findByHttpMethodIgnoreCaseOrHttpMethod(String httpMethod, String wildcard);
}
