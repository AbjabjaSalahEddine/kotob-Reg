package com.example.apigateway.api_gateway.routes;


import com.example.apigateway.service_discovery.entity.Service;
import com.example.apigateway.service_discovery.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/guest")
@RequiredArgsConstructor
@Slf4j
public class GuestApi {
    private final ServiceRepository serviceRepository;

    private String LIBRARIESMSNAME="Libraries_MS";
    private String SCANNEDBOOKSMSNAME="ScannedBooks_MS";


    public String getHostUrl(String name){
        List<Service> services= serviceRepository.findAll();
        List<Service> Instances= new ArrayList<>();
        services.stream().forEach(s -> {
            if(s.getName().equals(name)){
                Instances.add(s);
            }
        });
        Random rand = new Random();
        if(Instances.size()>0){
            return Instances.get(rand.nextInt(Instances.size())).getUrl();
        }else{
            return "None";
        }
    }



    @GetMapping("/searchbook")
    public ResponseEntity<?> searchbook(@RequestParam(value = "q") String query, Principal principal) {

        String url= "http://"+getHostUrl(LIBRARIESMSNAME)+"/books/search/"+query;
        log.info(url);
        try{
            List<?> result=new RestTemplate().getForEntity(url,  List.class).getBody();
            return new ResponseEntity<List>(result, HttpStatus.OK);
        }catch (RestClientException e){
            Map map = new HashMap<String,String>();
            map.put("msg","Service is down");
            return new ResponseEntity<Map>(map, HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("/searchquote")
    public ResponseEntity<?> searchquote(@RequestParam(value = "q") String query,@RequestParam(value = "lang") String lan, Principal principal) {

        String url1= "http://"+getHostUrl(SCANNEDBOOKSMSNAME)+"/search/?q="+query+"&lang="+lan;
        String url2= "http://"+getHostUrl(LIBRARIESMSNAME)+"/booksWithIds";

        try{
            List<Map> r=new RestTemplate().getForEntity(url1,  List.class).getBody();

            List<Long> ids=r.stream().map(b-> ((Number) b.get("id")).longValue()).collect(Collectors.toList());
            try {
                log.info(url2);
                List<Map> result = new RestTemplate().postForEntity(url2, ids, List.class).getBody();
                return new ResponseEntity<List>(result, HttpStatus.OK);

            }catch (RestClientException e){
                Map map = new HashMap<String,String>();
                map.put("msg","Service Libraries is down probably");
                return new ResponseEntity<Map>(map, HttpStatus.BAD_GATEWAY);
            }
//            List<?> result=new RestTemplate().getForEntity(url,  List.class).getBody();


        }catch (RestClientException e){
            Map map = new HashMap<String,String>();
            map.put("msg","Service ScannedBooks is down probably");
            return new ResponseEntity<Map>(map, HttpStatus.BAD_GATEWAY);
        }
    }
}
