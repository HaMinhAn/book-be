package com.example.candy.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BookRequest {
    private String title;
    private String author;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer stockQuantity;
    private String isbn;
    private String category;
    private Integer publishYear;
}
