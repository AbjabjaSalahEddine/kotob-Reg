package com.example.apigateway.authentication.service;

import com.example.apigateway.authentication.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONObject;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    boolean existUserById(String id);

    boolean isAllowedToManipulate(String id);

    boolean existUser(String email);

    User updateUser(String id, JSONObject user) throws NoSuchFieldException, IllegalAccessException, JsonProcessingException;

    void deleteUser(String id);



    void updateUserRole(String email, String roleName);
    User getUser(String email);
    List<User> getUsers();

    Object getUserById(String id);
}
