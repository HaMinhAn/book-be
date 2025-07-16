package com.example.candy.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.candy.dto.RegisterRequest;
import com.example.candy.services.AdminService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private com.example.candy.services.EmailService emailService;
    
    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdminUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            adminService.createAdminUser(registerRequest);
            return ResponseEntity.ok("Admin user created successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}
