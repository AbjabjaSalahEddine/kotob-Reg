package com.example.apigateway.authentication.service;

import com.example.apigateway.authentication.domain.Roles;
import com.example.apigateway.authentication.domain.User;
import com.example.apigateway.authentication.repo.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service @RequiredArgsConstructor @Transactional @Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findUserByEmail(email);
        if(user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        }
        else {
            log.info("User {} found in the database", email);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public boolean existUserById(String id) {
        return userRepo.existsById(id);
    }

    @Override
    public boolean isAllowedToManipulate(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = getUser(currentPrincipalName);

        String roleName = currentUser.getRole().name();
        return currentUser.getId().equals(id) || roleName.equals("ADMIN");
    }

    @Override
    public User saveUser(User user) {
        log.info("user saved {}", user.getFirstName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public boolean existUser(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public User updateUser(String id, JSONObject jsonObj) throws JsonProcessingException {
        log.info("user updated {}", jsonObj.toString());

        Iterator<String> keys = jsonObj.keys();
        log.info(jsonObj.toString());
        User user = userRepo.findById(id).get();

        while(keys.hasNext()) {

            String key = keys.next();
            if (key.equals("password")) {
                jsonObj.put(key, passwordEncoder.encode(jsonObj.getString(key)));
            }
            user.setField(key, jsonObj.getString(key));
        }
        log.info("!! {}", user.getFirstName());
        return userRepo.save(user);
    }



    @Override
    public void deleteUser(String id) {
        userRepo.deleteById(id);
    }


    @Override
    public void updateUserRole(String email, String roleName) {
        User user = userRepo.findUserByEmail(email);
        Roles role = Roles.valueOf(roleName);

        user.setRole(role);
        userRepo.save(user);

        log.info("Adding role {} to user {}", roleName, email);
    }

    @Override
    public User getUser(String email) {
        log.info("Fetching user {}", email);
        return userRepo.findUserByEmail(email);
    }
    @Override
    public User getUserById(String id) {
        log.info("Fetching user with id : {}", id);
        return userRepo.findUserById(id);
    }


    @Override
    public List<User> getUsers() {
        log.info("Fetching all users");
        return userRepo.findAll();
    }


}
