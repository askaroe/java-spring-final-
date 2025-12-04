package com.example.api_gateway.repository;

import com.example.api_gateway.entity.EndpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EndpointRepository extends JpaRepository<EndpointEntity, Long> {
    List<EndpointEntity> findByServiceAndMethod(String service, String method);
}

