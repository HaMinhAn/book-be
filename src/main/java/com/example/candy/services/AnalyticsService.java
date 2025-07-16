package com.example.candy.services;

import com.example.candy.dto.SalesAnalyticsResponse;
import com.example.candy.dto.SalesAnalyticsResponse.MonthlySales;
import com.example.candy.dto.SalesAnalyticsResponse.TopSellingBook;
import com.example.candy.dto.SalesAnalyticsResponse.CategorySales;
import com.example.candy.models.Order;
import com.example.candy.models.OrderItem;
import com.example.candy.models.OrderStatus;
import com.example.candy.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private OrderRepository orderRepository;

    public SalesAnalyticsResponse getSalesAnalytics() {
        List<Order> allOrders = orderRepository.findAll();
        
        // Filter completed orders
        List<Order> completedOrders = allOrders.stream()
            .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
            .collect(Collectors.toList());

        // Calculate basic metrics
        BigDecimal totalRevenue = completedOrders.stream()
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalOrders = (long) completedOrders.size();

        Long totalBooksSold = completedOrders.stream()
            .flatMap(order -> order.getItems().stream())
            .mapToLong(OrderItem::getQuantity)
            .sum();

        BigDecimal averageOrderValue = totalOrders > 0 
            ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        // Calculate monthly sales
        List<MonthlySales> monthlySales = calculateMonthlySales(completedOrders);

        // Calculate top selling books
        List<TopSellingBook> topSellingBooks = calculateTopSellingBooks(completedOrders);

        // Calculate category sales
        List<CategorySales> categorySales = calculateCategorySales(completedOrders);

        // Calculate order status distribution
        Map<String, Long> orderStatusDistribution = allOrders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getStatus().toString(),
                Collectors.counting()
            ));

        return new SalesAnalyticsResponse(
            totalRevenue,
            totalOrders,
            totalBooksSold,
            averageOrderValue,
            monthlySales,
            topSellingBooks,
            categorySales,
            orderStatusDistribution
        );
    }

    private List<MonthlySales> calculateMonthlySales(List<Order> orders) {
        Map<String, List<Order>> ordersByMonth = orders.stream()
            .collect(Collectors.groupingBy(order -> 
                order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
            ));

        return ordersByMonth.entrySet().stream()
            .map(entry -> {
                String month = entry.getKey();
                List<Order> monthOrders = entry.getValue();
                
                BigDecimal monthRevenue = monthOrders.stream()
                    .map(Order::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                Long orderCount = (long) monthOrders.size();
                
                Long booksSold = monthOrders.stream()
                    .flatMap(order -> order.getItems().stream())
                    .mapToLong(OrderItem::getQuantity)
                    .sum();
                
                return new MonthlySales(month, monthRevenue, orderCount, booksSold);
            })
            .sorted(Comparator.comparing(MonthlySales::getMonth))
            .collect(Collectors.toList());
    }

    private List<TopSellingBook> calculateTopSellingBooks(List<Order> orders) {
        Map<Long, List<OrderItem>> itemsByBook = orders.stream()
            .flatMap(order -> order.getItems().stream())
            .collect(Collectors.groupingBy(item -> item.getBook().getId()));

        return itemsByBook.entrySet().stream()
            .map(entry -> {
                Long bookId = entry.getKey();
                List<OrderItem> bookItems = entry.getValue();
                
                OrderItem firstItem = bookItems.get(0);
                String title = firstItem.getBook().getTitle();
                String author = firstItem.getBook().getAuthor();
                
                Long quantitySold = bookItems.stream()
                    .mapToLong(OrderItem::getQuantity)
                    .sum();
                
                BigDecimal revenue = bookItems.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                return new TopSellingBook(bookId, title, author, quantitySold, revenue);
            })
            .sorted(Comparator.comparing(TopSellingBook::getQuantitySold).reversed())
            .limit(10)
            .collect(Collectors.toList());
    }

    private List<CategorySales> calculateCategorySales(List<Order> orders) {
        Map<String, List<OrderItem>> itemsByCategory = orders.stream()
            .flatMap(order -> order.getItems().stream())
            .collect(Collectors.groupingBy(item -> 
                item.getBook().getCategory() != null ? item.getBook().getCategory() : "Unknown"
            ));

        return itemsByCategory.entrySet().stream()
            .map(entry -> {
                String category = entry.getKey();
                List<OrderItem> categoryItems = entry.getValue();
                
                Long booksSold = categoryItems.stream()
                    .mapToLong(OrderItem::getQuantity)
                    .sum();
                
                BigDecimal revenue = categoryItems.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                Long orderCount = categoryItems.stream()
                    .map(item -> item.getOrder().getId())
                    .distinct()
                    .count();
                
                return new CategorySales(category, booksSold, revenue, orderCount);
            })
            .sorted(Comparator.comparing(CategorySales::getRevenue).reversed())
            .collect(Collectors.toList());
    }
}
