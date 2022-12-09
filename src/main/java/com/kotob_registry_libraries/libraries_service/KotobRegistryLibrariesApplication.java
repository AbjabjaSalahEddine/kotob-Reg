package com.kotob_registry_libraries.libraries_service;

import com.kotob_registry_libraries.libraries_service.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class KotobRegistryLibrariesApplication {
	@Autowired
	private BookRepository bookRepository;
	@Value("${server.port}")
	private Integer serverPort;
	private String addMe(){
		try {
			RestTemplate restTemplate=new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Secret_Key");
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			Map<String, String> map = new HashMap<>();
			map.put("name", "Libraries_MS");
			map.put("port", serverPort.toString());

			HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, headers);

			return restTemplate.exchange("http://localhost:8080/service-discovery/", HttpMethod.POST, entity, String.class).getBody();
		}catch (RestClientException e){
			return "Service Discovery 'http://localhost:8080/service-discovery/' is down probably!";
		}

	}


	public static void main(String[] args) {

		SpringApplication.run(KotobRegistryLibrariesApplication.class, args);
	}
	@Bean
	CommandLineRunner start(RepositoryRestConfiguration repositoryRestConfiguration) {
		return args -> {

			log.info(addMe());
//			log.info(bookRepository.getBookCartData(1L).toString());
		};
	}

}
