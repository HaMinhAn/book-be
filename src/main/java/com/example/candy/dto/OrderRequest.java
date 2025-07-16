package com.example.candy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {
    
    @NotNull(message = "Shipping information is required")
    @Valid
    private ShippingInfo shippingInfo;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    // Constructors
    public OrderRequest() {}

    public OrderRequest(ShippingInfo shippingInfo, String paymentMethod) {
        this.shippingInfo = shippingInfo;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
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

    // Nested ShippingInfo class
    public static class ShippingInfo {
        @NotBlank(message = "First name is required")
        private String firstName;
        
        @NotBlank(message = "Last name is required")
        private String lastName;
        
        @NotBlank(message = "Address is required")
        private String address;
        
        @NotBlank(message = "City is required")
        private String city;
        
        @NotBlank(message = "State is required")
        private String state;
        
        @NotBlank(message = "ZIP code is required")
        private String zipCode;
        
        @NotBlank(message = "Email is required")
        private String email;
        
        @NotBlank(message = "Phone is required")
        private String phone;

        // Constructors
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
