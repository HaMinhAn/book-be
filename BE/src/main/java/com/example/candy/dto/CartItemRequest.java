package com.example.candy.dto;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long bookId;
    private Integer quantity;
}
