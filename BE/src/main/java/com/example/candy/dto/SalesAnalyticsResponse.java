package com.example.candy.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsResponse {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long totalBooksSold;
    private BigDecimal averageOrderValue;
    private List<MonthlySales> monthlySales;
    private List<TopSellingBook> topSellingBooks;
    private List<CategorySales> categorySales;
    private Map<String, Long> orderStatusDistribution;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlySales {
        private String month;
        private BigDecimal revenue;
        private Long orderCount;
        private Long booksSold;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopSellingBook {
        private Long bookId;
        private String title;
        private String author;
        private Long quantitySold;
        private BigDecimal revenue;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySales {
        private String category;
        private Long booksSold;
        private BigDecimal revenue;
        private Long orderCount;
    }
}
