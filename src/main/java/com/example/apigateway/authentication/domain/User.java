package com.example.apigateway.authentication.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Field;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> libraries;

    private Roles role;

    public User(String email, String password, Roles role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }


    public void setField(String fieldName, String value) {
        try {
            Field field = getClass().getDeclaredField(fieldName);
            field.set(this, value);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
