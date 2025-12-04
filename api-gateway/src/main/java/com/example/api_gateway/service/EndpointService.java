package com.example.api_gateway.service;

import com.example.api_gateway.entity.EndpointEntity;
import com.example.api_gateway.repository.EndpointRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndpointService {

    private final EndpointRepository repository;

    public EndpointService(EndpointRepository repository) {
        this.repository = repository;
    }

    public EndpointEntity create(EndpointEntity e) {
        return repository.save(e);
    }

    public List<EndpointEntity> findByServiceAndMethod(String service, String method) {
        return repository.findByServiceAndMethod(service, method);
    }
}

