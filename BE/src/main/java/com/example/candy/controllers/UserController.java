package com.example.candy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.candy.dto.UserInfoResponse;
import com.example.candy.services.UserDetailsImpl;
import com.example.candy.services.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserInfo() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        try {
            UserInfoResponse response = userService.getUserInfo(userDetails.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserInfoResponse userInfo) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        try {
            boolean updated = userService.updateUserInfo(userDetails.getId(), userInfo);
            if (updated) {
                return ResponseEntity.ok("User information updated successfully");
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
