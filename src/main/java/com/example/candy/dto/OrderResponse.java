package com.example.candy.dto;

import com.example.candy.models.Order;
import com.example.candy.models.OrderItem;
import com.example.candy.models.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private ShippingInfo shippingInfo;
    private String paymentMethod;

    // Constructors
    public OrderResponse() {}

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.orderDate = order.getOrderDate();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.items = order.getItems().stream()
            .map(OrderItemResponse::new)
            .collect(Collectors.toList());
        this.shippingInfo = new ShippingInfo(
            order.getFirstName(),
            order.getLastName(),
            order.getAddress(),
            order.getCity(),
            order.getState(),
            order.getZipCode(),
            order.getEmail(),
            order.getPhone()
        );
        this.paymentMethod = order.getPaymentMethod();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Nested classes
    public static class OrderItemResponse {
        private Long id;
        private BookInfo book;
        private Integer quantity;
        private BigDecimal price;

        public OrderItemResponse() {}

        public OrderItemResponse(OrderItem orderItem) {
            this.id = orderItem.getId();
            this.book = new BookInfo(orderItem.getBook());
            this.quantity = orderItem.getQuantity();
            this.price = orderItem.getPrice();
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public BookInfo getBook() {
            return book;
        }

        public void setBook(BookInfo book) {
            this.book = book;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    public static class BookInfo {
        private Long id;
        private String title;
        private String author;
        private BigDecimal price;

        public BookInfo() {}

        public BookInfo(com.example.candy.models.Book book) {
            this.id = book.getId();
            this.title = book.getTitle();
            this.author = book.getAuthor();
            this.price = book.getPrice();
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    public static class ShippingInfo {
        private String firstName;
        private String lastName;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String email;
        private String phone;

        public ShippingInfo() {}

        public ShippingInfo(String firstName, String lastName, String address, String city,
                           String state, String zipCode, String email, String phone) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.city = city;
            this.state = state;
            this.zipCode = zipCode;
            this.email = email;
            this.phone = phone;
        }

        // Getters and Setters
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
