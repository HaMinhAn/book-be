package com.example.candy.services;

import com.example.candy.dto.OrderRequest;
import com.example.candy.dto.OrderResponse;
import com.example.candy.models.*;
import com.example.candy.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, User user) {
        // Get user's cart
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order with empty cart");
        }

        // Create new order
        OrderRequest.ShippingInfo shippingInfo = orderRequest.getShippingInfo();
        Order order = new Order(
            user,
            BigDecimal.ZERO, // Will be calculated from items
            shippingInfo.getFirstName(),
            shippingInfo.getLastName(),
            shippingInfo.getAddress(),
            shippingInfo.getCity(),
            shippingInfo.getState(),
            shippingInfo.getZipCode(),
            shippingInfo.getEmail(),
            shippingInfo.getPhone(),
            orderRequest.getPaymentMethod()
        );

        // Create order items from cart items
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Book book = cartItem.getBook();
            
            // Check stock availability
            if (book.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for book: " + book.getTitle());
            }

            // Create order item
            OrderItem orderItem = new OrderItem(
                order,
                book,
                cartItem.getQuantity(),
                book.getPrice()
            );
            orderItems.add(orderItem);

            // Calculate total
            BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            // Update book stock
            book.setStockQuantity(book.getStockQuantity() - cartItem.getQuantity());
            bookRepository.save(book);
        }

        // Set total amount and items
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear the cart after successful order
        cart.getItems().clear();
        cartRepository.save(cart);

        // Send order confirmation email
        try {            
            emailService.sendOrderConfirmationEmail(savedOrder);
            logger.info("Successfully triggered order confirmation email for Order #{}", savedOrder.getId());
        } catch (Exception e) {
            // Log the error but don't roll back the transaction
            logger.error("Failed to send order confirmation email for Order #{}: {}", savedOrder.getId(), e.getMessage());
           
        }

        return new OrderResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(User user) {
        List<Order> orders = orderRepository.findByUserWithItemsAndBooks(user);
        return orders.stream()
            .map(OrderResponse::new)
            .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, User user) {
        Order order = orderRepository.findByIdAndUserWithItemsAndBooks(orderId, user)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponse(order);
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findByIdWithItemsAndBooks(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        
        // Send order status update email
        try {           
            emailService.sendOrderStatusUpdateEmail(savedOrder);
            logger.info("Successfully triggered status update email for Order #{}", savedOrder.getId());
        } catch (Exception e) {
            // Log the error but don't roll back the transaction
            logger.error("Failed to send order status update email for Order #{}: {}", 
                        savedOrder.getId(), e.getMessage());
            logger.debug("Detailed error when sending status update email:", e);
        }
        
        return new OrderResponse(savedOrder);
    }

    // Admin methods
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
            .map(OrderResponse::new)
            .collect(Collectors.toList());
    }

    public OrderResponse getOrderByIdAdmin(Long orderId) {
        Order order = orderRepository.findByIdWithItemsAndBooks(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponse(order);
    }
    
    @Transactional
    public OrderResponse confirmOrderReceived(Long orderId, User user) {
        // Get the order and verify it belongs to the user
        Order order = orderRepository.findByIdAndUserWithItemsAndBooks(orderId, user)
            .orElseThrow(() -> new RuntimeException("Order not found or does not belong to the current user"));
        
        // Verify the order status is SHIPPED - only shipped orders can be confirmed as received
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("Only shipped orders can be confirmed as received. Current status: " + order.getStatus());
        }
        
        // Update the status to DELIVERED
        order.setStatus(OrderStatus.DELIVERED);
        Order savedOrder = orderRepository.save(order);
        
        // Send order delivered confirmation email
        try {           
            emailService.sendOrderDeliveredEmail(savedOrder);
            logger.info("Successfully triggered order delivered email for Order #{}", savedOrder.getId());
        } catch (Exception e) {
            // Log the error but don't roll back the transaction
            logger.error("Failed to send order delivered email for Order #{}: {}", 
                        savedOrder.getId(), e.getMessage());
            logger.debug("Detailed error when sending order delivered email:", e);
        }
        
        return new OrderResponse(savedOrder);
    }

    public Map<String, Object> getUserOrdersWithFilters(
            User user, OrderStatus status, String startDate, String endDate, 
            Double minAmount, Double maxAmount, int page, int size) {
        
        // Build dynamic query criteria
        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // User filter
            predicates.add(criteriaBuilder.equal(root.get("user"), user));
            
            // Status filter
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // Date range filter
            if (startDate != null && !startDate.isEmpty()) {
                try {
                    LocalDate start = LocalDate.parse(startDate);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("orderDate"), start.atStartOfDay()));
                } catch (DateTimeParseException e) {
                    logger.warn("Invalid start date format: {}", startDate);
                }
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                try {
                    LocalDate end = LocalDate.parse(endDate);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("orderDate"), end.atTime(LocalTime.MAX)));
                } catch (DateTimeParseException e) {
                    logger.warn("Invalid end date format: {}", endDate);
                }
            }
            
            // Amount range filter
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("totalAmount"), BigDecimal.valueOf(minAmount)));
            }
            
            if (maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("totalAmount"), BigDecimal.valueOf(maxAmount)));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // Create pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        
        // Get paginated results
        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);
        
        // Convert to response DTOs
        List<OrderResponse> content = ordersPage.getContent().stream()
            .map(OrderResponse::new)
            .collect(Collectors.toList());
        
        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("currentPage", ordersPage.getNumber());
        response.put("totalItems", ordersPage.getTotalElements());
        response.put("totalPages", ordersPage.getTotalPages());
        
        return response;
    }

    public Map<String, Object> getAllOrdersWithFilters(
            OrderStatus status, String startDate, String endDate, 
            Double minAmount, Double maxAmount, Long userId, int page, int size) {
        
        // Add debug logging
        logger.info("Fetching orders with pagination: page={}, size={}", page, size);
        
        // Build dynamic query criteria
        Specification<Order> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Status filter
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // Date range filter
            if (startDate != null && !startDate.isEmpty()) {
                try {
                    LocalDate start = LocalDate.parse(startDate);
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("orderDate"), start.atStartOfDay()));
                } catch (DateTimeParseException e) {
                    logger.warn("Invalid start date format: {}", startDate);
                }
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                try {
                    LocalDate end = LocalDate.parse(endDate);
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("orderDate"), end.atTime(LocalTime.MAX)));
                } catch (DateTimeParseException e) {
                    logger.warn("Invalid end date format: {}", endDate);
                }
            }
            
            // Amount range filter
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("totalAmount"), BigDecimal.valueOf(minAmount)));
            }
            
            if (maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("totalAmount"), BigDecimal.valueOf(maxAmount)));
            }
            
            // User filter (admin-specific)
            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // Create pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        logger.info("Created pageable request: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
        // Get paginated results with eager loading of relationships
        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);
        logger.info("Received page result: total elements={}, total pages={}, current page={}",
                 ordersPage.getTotalElements(), ordersPage.getTotalPages(), ordersPage.getNumber());
        
        // To ensure we're getting pagination, limit content to the right size
        List<Order> paginatedOrders = ordersPage.getContent();
        logger.info("Number of orders in current page: {}", paginatedOrders.size());
        
        // Load all order items and books for each order to avoid LazyInitializationException
        List<OrderResponse> content = paginatedOrders.stream()
            .map(order -> {
                // Fetch order items and books if not already loaded
                if (order.getItems() != null) {
                    order.getItems().forEach(item -> {
                        if (item.getBook() != null) {
                            // Access book properties to force loading
                            item.getBook().getTitle();
                        }
                    });
                }
                return new OrderResponse(order);
            })
            .collect(Collectors.toList());
        
        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("content", content);
        response.put("currentPage", ordersPage.getNumber());
        response.put("totalItems", ordersPage.getTotalElements());
        response.put("totalPages", ordersPage.getTotalPages());
        
        return response;
    }
}
