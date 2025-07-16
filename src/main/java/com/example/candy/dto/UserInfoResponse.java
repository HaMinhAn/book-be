package com.example.candy.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private List<String> roles;
}
