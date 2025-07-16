package com.example.candy.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartRequest {
    private List<CartItemRequest> items;
}
