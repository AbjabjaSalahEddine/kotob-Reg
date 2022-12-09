package com.example.apigateway.service_discovery.api;

import com.example.apigateway.service_discovery.entity.Service;

import java.util.List;

public interface ServiceApi {
    public List<Service> checkServices();


    public void updateServicesDataBase();


}
