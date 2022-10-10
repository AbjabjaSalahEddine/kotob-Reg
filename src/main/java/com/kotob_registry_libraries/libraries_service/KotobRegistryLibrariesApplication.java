package com.kotob_registry_libraries.libraries_service;

import com.kotob_registry_libraries.libraries_service.entity.Book;
import com.kotob_registry_libraries.libraries_service.entity.Library;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.stereotype.Repository;

@SpringBootApplication
public class KotobRegistryLibrariesApplication {

	public static void main(String[] args) {

		SpringApplication.run(KotobRegistryLibrariesApplication.class, args);
	}
	@Bean
	CommandLineRunner start(RepositoryRestConfiguration repositoryRestConfiguration) {
		return args -> {
			repositoryRestConfiguration.exposeIdsFor(Book.class);
			repositoryRestConfiguration.exposeIdsFor(Library.class);
		};
	}

}
