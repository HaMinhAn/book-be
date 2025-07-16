package com.example.candy.services;

import com.example.candy.dto.RegisterRequest;
import com.example.candy.dto.UserInfoResponse;
import com.example.candy.models.ERole;
import com.example.candy.models.Role;
import com.example.candy.models.User;
import com.example.candy.repositories.RoleRepository;
import com.example.candy.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    public void registerUser(@Valid RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setAddress(signUpRequest.getAddress());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        userRepository.save(user);
    }

    public UserInfoResponse getUserInfo(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();
        UserInfoResponse info = new UserInfoResponse();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setEmail(user.getEmail());
        info.setFirstName(user.getFirstName());
        info.setLastName(user.getLastName());
        info.setAddress(user.getAddress());
        info.setPhoneNumber(user.getPhoneNumber());
        
        // Add roles
        java.util.List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(java.util.stream.Collectors.toList());
        info.setRoles(roles);
        
        return info;
    }

    public boolean updateUserInfo(Long userId, UserInfoResponse userInfo) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setAddress(userInfo.getAddress());
        user.setPhoneNumber(userInfo.getPhoneNumber());
        userRepository.save(user);
        return true;
    }

    public User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
