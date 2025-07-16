package com.example.candy.dto;

import lombok.Data;
import java.util.Set;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private Set<String> roles;
}
