package com.example.apigateway;

import com.example.apigateway.authentication.service.UserService;
import com.example.apigateway.service_discovery.api.ServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
@Slf4j
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }




    @Bean
    CommandLineRunner run(UserService userService, ServiceApi serviceApi) {
        return args -> {
            serviceApi.checkServices();
//            userService.saveUser(new User("admin@kotobrepo.ma", "admin", null));
//            userService.saveUser(new
//                    User(null, "youness", "aabaoui", "youness@gmail.com", "youness", new ArrayList<>(), null));
//            userService.saveUser(new
//                    User(null, "alae", "abjabja", "alae@gmail.com", "alae", new ArrayList<>(), null));
//
//            userService.updateUserRole("admin@kotobrepo.ma", "ADMIN");
//            userService.updateUserRole("youness@gmail.com", "LIBRARY_OWNER");
//            userService.updateUserRole("alae@gmail.com", "LIBRARY_OWNER");
        };
    }

}
