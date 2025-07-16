package com.example.candy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a basic controller for health check.
 * The main API functionality has been moved to dedicated controllers:
 * - AuthController - for login/register
 * - UserController - for user information
 * - BookController - for book management
 * - CartController - for cart operations
 */
@RestController
public class controllers {
  @GetMapping("/api/health")
  public String healthCheck() {
    return "Service is up and running!";
  }
}
