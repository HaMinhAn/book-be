package com.example.candy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.candy.dto.CartItemRequest;
import com.example.candy.dto.CartRequest;
import com.example.candy.dto.CartResponse;
import com.example.candy.services.UserDetailsImpl;
import com.example.candy.services.CartService;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserCart() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        Optional<CartResponse> cartOpt = cartService.getUserCart(userId);
        return ResponseEntity.ok(cartOpt.orElse(new CartResponse()));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrUpdateCart(@RequestBody CartRequest cartRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        try {
            CartResponse cartResponse = cartService.createOrUpdateCart(userId, cartRequest);
            return ResponseEntity.ok(cartResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteCart() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        boolean deleted = cartService.deleteCart(userId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/item")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addItemToCart(@RequestBody CartItemRequest itemRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        try {
            CartResponse cartResponse = cartService.addItemToCart(userId, itemRequest);
            return ResponseEntity.ok(cartResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/item/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long bookId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        Optional<CartResponse> cartOpt = cartService.removeItemFromCart(userId, bookId);
        
        if (cartOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found in cart");
        }
        
        return ResponseEntity.ok(cartOpt.get());
    }

    @PutMapping("/item/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable Long bookId, @RequestBody Map<String, Integer> request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        Integer newQuantity = request.get("quantity");
        if (newQuantity == null || newQuantity < 0) {
            return ResponseEntity.badRequest().body("Invalid quantity");
        }

        try {
            CartResponse cartResponse = cartService.updateItemQuantity(userId, bookId, newQuantity);
            return ResponseEntity.ok(cartResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

