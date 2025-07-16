package com.example.candy.controllers;

import com.example.candy.dto.OrderRequest;
import com.example.candy.dto.OrderResponse;
import com.example.candy.models.OrderStatus;
import com.example.candy.models.User;
import com.example.candy.services.OrderService;
import com.example.candy.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/orders")
    @Operation(
        summary = "Create a new order",
        description = "Creates a new order from the user's current cart items",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid order data or empty cart")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            Authentication authentication) {
        try {
            logger.info("Creating order for user: {}", authentication.getName());
            
            User user = userService.getCurrentUser(authentication);
            OrderResponse orderResponse = orderService.createOrder(orderRequest, user);
            
            logger.info("Order created successfully with ID: {}", orderResponse.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
            
        } catch (RuntimeException e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to create order",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Unexpected error creating order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal server error",
                "message", "An unexpected error occurred while creating the order"
            ));
        }
    }

    @GetMapping("/orders")
    @Operation(
        summary = "Get user's orders",
        description = "Retrieves all orders for the authenticated user with optional filtering and pagination",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<?> getUserOrders(
            Authentication authentication,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("Getting filtered orders for user: {}", authentication.getName());
            
            User user = userService.getCurrentUser(authentication);
            
            Map<String, Object> response = orderService.getUserOrdersWithFilters(
                user, status, startDate, endDate, minAmount, maxAmount, page, size);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting user orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal server error",
                "message", "An unexpected error occurred while retrieving orders"
            ));
        }
    }

    @GetMapping("/orders/{orderId}")
    @Operation(
        summary = "Get order by ID",
        description = "Retrieves a specific order by ID for the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {
        try {
            logger.info("Getting order {} for user: {}", orderId, authentication.getName());
            
            User user = userService.getCurrentUser(authentication);
            OrderResponse order = orderService.getOrderById(orderId, user);
            
            return ResponseEntity.ok(order);
            
        } catch (RuntimeException e) {
            logger.error("Order not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting order by ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal server error",
                "message", "An unexpected error occurred while retrieving the order"
            ));
        }
    }

    // Admin endpoints
    @GetMapping("/admin/orders")
    @Operation(
        summary = "Get all orders (Admin)",
        description = "Retrieves all orders in the system with optional filtering and pagination (Admin only)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    public ResponseEntity<?> getAllOrders(
            Authentication authentication,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("Admin getting filtered orders: {}", authentication.getName());
            
            Map<String, Object> response = orderService.getAllOrdersWithFilters(
                status, startDate, endDate, minAmount, maxAmount, userId, page, size);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting all orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal server error",
                "message", "An unexpected error occurred while retrieving orders"
            ));
        }
    }

    @GetMapping("/admin/orders/{orderId}")
    @Operation(
        summary = "Get order by ID (Admin)",
        description = "Retrieves a specific order by ID (Admin only)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    public ResponseEntity<?> getOrderByIdAdmin(@PathVariable Long orderId) {
        try {
            logger.info("Admin getting order: {}", orderId);
            
            OrderResponse order = orderService.getOrderByIdAdmin(orderId);
            return ResponseEntity.ok(order);
            
        } catch (RuntimeException e) {
            logger.error("Order not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error getting order by ID (admin)", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal server error",
                "message", "An unexpected error occurred while retrieving the order"
            ));
        }
    }

    @PutMapping("/admin/orders/{orderId}/status")
    @Operation(
        summary = "Update order status (Admin)",
        description = "Updates the status of an order (Admin only)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        try {
            logger.info("Admin updating order {} status to: {}", orderId, status);
            
            OrderResponse order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
            
        } catch (RuntimeException e) {
            logger.error("Order not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating order status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal server error",
                "message", "An unexpected error occurred while updating the order status"
            ));
        }
    }
    
    @PostMapping("/orders/{orderId}/confirm-received")
    @Operation(
        summary = "Confirm order as received",
        description = "Updates the status of an order to DELIVERED when the user confirms receipt",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Order confirmed as received successfully")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "403", description = "Access denied - User does not own this order")
    public ResponseEntity<?> confirmOrderReceived(
            @PathVariable Long orderId,
            Authentication authentication) {
        try {
            logger.info("User {} confirming order {} as received", authentication.getName(), orderId);
            
            User user = userService.getCurrentUser(authentication);
            OrderResponse order = orderService.confirmOrderReceived(orderId, user);
            
            return ResponseEntity.ok(order);
            
        } catch (RuntimeException e) {
            logger.error("Error confirming order receipt: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to confirm order receipt",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Unexpected error confirming order receipt", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Internal server error",
                "message", "An unexpected error occurred while confirming the order receipt"
            ));
        }
    }
}
