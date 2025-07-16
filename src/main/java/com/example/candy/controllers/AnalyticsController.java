package com.example.candy.controllers;

import com.example.candy.dto.SalesAnalyticsResponse;
import com.example.candy.services.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Analytics", description = "Sales analytics endpoints")
public class AnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/analytics/sales")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get sales analytics",
        description = "Retrieves comprehensive sales analytics including revenue, orders, and top-selling books (Admin only)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    public ResponseEntity<?> getSalesAnalytics(Authentication authentication) {
        try {
            logger.info("Admin {} requesting sales analytics", authentication.getName());
            
            SalesAnalyticsResponse analytics = analyticsService.getSalesAnalytics();
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("Error getting sales analytics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal server error",
                "message", "An unexpected error occurred while retrieving analytics"
            ));
        }
    }
}
