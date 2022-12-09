package com.example.apigateway.authentication.repo;


import com.example.apigateway.authentication.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface UserRepo extends MongoRepository<User, String> {
    User findUserByEmail(String email);
    boolean existsByEmail(String email);

    User findUserById(String id);

}
