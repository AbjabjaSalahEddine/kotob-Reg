package com.example.apigateway.service_discovery.api;


import com.example.apigateway.service_discovery.entity.Service;
import com.example.apigateway.service_discovery.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/service-discovery")
@RequiredArgsConstructor
@Slf4j
public class ServiceApiImpl implements ServiceApi {
    private final ServiceRepository serviceRepository;

    public List<Service> checkServices() {
        List<Service> runningservices= new ArrayList<Service>();
        List<Service> services=serviceRepository.findAll();
            services.stream().forEach(s -> {
                try {
                    String response =new RestTemplate().getForEntity("http://"+s.getUrl()+"/actuator/health",  String.class).getBody();
                    log.info(s.getName()+" --- > "+response);
                    if(response.contains("UP")){
                        runningservices.add(s);
                    }
                }catch (RestClientException e){
                    log.info(s.getName()+" --- > Cant connect !!");
                    serviceRepository.delete(s);
                }
            });
        return runningservices;
    }





    @PostMapping("/")
    public ResponseEntity<?> addMe(@RequestBody Map<String, Object> payload
            , @RequestHeader(HttpHeaders.AUTHORIZATION) String Auth
            , HttpServletRequest request) {


        if ("Secret_Key".equals(Auth)){
            Service service=new Service();
            String url= request.getRemoteHost();
            try {
                service.setUrl(url+":"+payload.get("port"));
                service.setName((String) payload.get("name"));
                serviceRepository.save(service);

                Map map=new HashMap<String, String>();
                map.put("msg", "Done");
                return ResponseEntity.status(200).body(map);
            }catch (Exception e){
                Map map=new HashMap<String, String>();
                map.put("err", e.getMessage());
                return ResponseEntity.status(400).body(map);
            }

        }
        Map map=new HashMap<String, String>();
        map.put("err", "Not every client can be recognized as a service");

        return ResponseEntity.status(500).body(map);

    }
    @GetMapping("/")
    public ResponseEntity<?> checkHealths() {
        List<Service> runningservices=checkServices();
        return ResponseEntity.status(200).body(runningservices);
    }

    @Scheduled(fixedDelay = 5000)
    public void updateServicesDataBase() {
        log.info("Number of instances Running is :"+String.valueOf(checkServices().size())+"\n " +
                "For more details check http://localhost:8080/service-discovery/");
    }

}

