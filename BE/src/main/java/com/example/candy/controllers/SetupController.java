package com.example.candy.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.candy.dto.RegisterRequest;
import com.example.candy.services.AdminService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/setup")
public class SetupController {
    
    @Value("${app.setup.secret:defaultSetupSecret}")
    private String setupSecret;
    
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/init-admin")
    public ResponseEntity<?> initializeAdmin(
            @RequestHeader("X-Setup-Secret") String secret,
            @Valid @RequestBody RegisterRequest registerRequest) {
        
        if (!setupSecret.equals(secret)) {
            return ResponseEntity.status(403).body("Invalid setup secret");
        }
        
        try {
            adminService.createAdminUser(registerRequest);
            return ResponseEntity.ok("Initial admin user created successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
