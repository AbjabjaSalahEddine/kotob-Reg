package com.example.apigateway.service_discovery.repository;

import com.example.apigateway.service_discovery.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ServiceRepository extends JpaRepository<Service, Integer> {

    List<Service> findByName(String name);

}
