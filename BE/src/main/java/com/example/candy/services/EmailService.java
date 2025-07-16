package com.example.candy.services;

import com.example.candy.models.Order;
import com.example.candy.models.OrderItem;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Date;
import java.util.Date;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;
    
    // Log mail configuration at startup
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        logger.info("EmailService initialized with mailSender: {}", mailSender.getClass().getName());
    }

    public void sendOrderConfirmationEmail(Order order) {
        logger.info("Preparing to send order confirmation email for Order #{}", order.getId());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            String recipientEmail = order.getEmail();
            logger.debug("Setting email recipient to: {}", recipientEmail);
            helper.setTo(recipientEmail);
            
            String subject = "Order Confirmation #" + order.getId() + " - Candy Book Store";
            logger.debug("Setting email subject to: {}", subject);
            helper.setSubject(subject);
            
            // Generate HTML email content
            logger.debug("Generating HTML content for order confirmation email");
            String emailContent = generateOrderConfirmationHtml(order);
            helper.setText(emailContent, true);
            
            // Log MIME message details for debugging
            logger.debug("Email MIME message details: contentType={}, encoding={}, messageId={}",
                    message.getContentType(),
                    message.getEncoding(),
                    message.getMessageID());
            
            logger.info("Sending order confirmation email to {} for Order #{}", recipientEmail, order.getId());
            mailSender.send(message);
            logger.info("Successfully sent order confirmation email for Order #{}", order.getId());
        } catch (MessagingException e) {
            logger.error("Failed to send order confirmation email for Order #{}: {}", order.getId(), e.getMessage());
            logger.debug("Detailed error when sending confirmation email:", e);
        } catch (Exception e) {
            logger.error("Unexpected error when sending order confirmation email for Order #{}: {}", order.getId(), e.getMessage());
            logger.debug("Detailed unexpected error:", e);
        }
    }
    
    public void sendOrderStatusUpdateEmail(Order order) {
        logger.info("Preparing to send order status update email for Order #{} with status: {}", order.getId(), order.getStatus());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            String recipientEmail = order.getEmail();
            logger.debug("Setting email recipient to: {}", recipientEmail);
            helper.setTo(recipientEmail);
            
            String subject = "Order #" + order.getId() + " Status Update - Candy Book Store";
            logger.debug("Setting email subject to: {}", subject);
            helper.setSubject(subject);
            
            // Generate HTML email content
            logger.debug("Generating HTML content for status update email with status: {}", order.getStatus());
            String emailContent = generateOrderStatusUpdateHtml(order);
            helper.setText(emailContent, true);
            
            // Log MIME message details for debugging
            logger.debug("Email MIME message details: contentType={}, encoding={}, messageId={}",
                    message.getContentType(),
                    message.getEncoding(),
                    message.getMessageID());
            
            logger.info("Sending order status update email to {} for Order #{} (Status: {})", 
                       recipientEmail, order.getId(), order.getStatus());
            mailSender.send(message);
            logger.info("Successfully sent order status update email for Order #{}", order.getId());
        } catch (MessagingException e) {
            logger.error("Failed to send order status update email for Order #{}: {}", order.getId(), e.getMessage());
            logger.debug("Detailed error when sending status update email:", e);
        } catch (Exception e) {
            logger.error("Unexpected error when sending order status update email for Order #{}: {}", order.getId(), e.getMessage());
            logger.debug("Detailed unexpected error:", e);
        }
    }
    
    private String generateOrderStatusUpdateHtml(Order order) {
        logger.debug("Generating HTML for order status update email - Order ID: {}, Status: {}", order.getId(), order.getStatus());
        
        try {
            StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>")
            .append("<html>")
            .append("<head>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 650px; margin: 0 auto; }")
            .append(".header { background-color: #1976d2; color: white; padding: 20px; text-align: center; }")
            .append(".content { padding: 20px; }")
            .append(".status-box { background-color: #f9f9f9; border: 1px solid #ddd; padding: 20px; margin-bottom: 20px; text-align: center; }")
            .append(".status-pending { background-color: #fff3cd; color: #856404; }")
            .append(".status-processing { background-color: #d1ecf1; color: #0c5460; }")
            .append(".status-shipped { background-color: #d4edda; color: #155724; }")
            .append(".status-delivered { background-color: #d4edda; color: #155724; font-weight: bold; }")
            .append(".status-cancelled { background-color: #f8d7da; color: #721c24; }")
            .append(".footer { background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class='header'>")
            .append("<h1>Order Status Update</h1>")
            .append("</div>")
            .append("<div class='content'>")
            .append("<p>Dear ").append(order.getFirstName()).append(" ").append(order.getLastName()).append(",</p>")
            .append("<p>The status of your order #").append(order.getId()).append(" has been updated.</p>")
            
            // Status box
            .append("<div class='status-box status-").append(order.getStatus().toString().toLowerCase()).append("'>")
            .append("<h2>Your order is now: ").append(order.getStatus()).append("</h2>");
            
        // Add status-specific message
        switch (order.getStatus()) {
            case PENDING:
                html.append("<p>We've received your order and are processing it. We'll notify you when it ships.</p>");
                break;
            case PROCESSING:
                html.append("<p>Your order is being prepared. We'll notify you when it ships.</p>");
                break;
            case SHIPPED:
                html.append("<p>Your order is on its way! You should receive it within the next few business days.</p>");
                break;
            case DELIVERED:
                html.append("<p>Your order has been delivered. We hope you enjoy your books!</p>");
                break;
            case CANCELLED:
                html.append("<p>Your order has been cancelled. If you have any questions, please contact our customer support.</p>");
                break;
            default:
                html.append("<p>Thank you for shopping with us!</p>");
        }
        
        html.append("</div>")
            
            // Order summary link
            .append("<p>To view your order details, please <a href='http://localhost:3000/my-orders/").append(order.getId()).append("'>click here</a>.</p>")
            .append("<p>If you have any questions about your order, please contact our customer support at <a href='mailto:support@candybookstore.com'>support@candybookstore.com</a></p>")
            .append("</div>")
            .append("<div class='footer'>")
            .append("<p>&copy; 2025 Candy Book Store. All rights reserved.</p>")
            .append("<p>This is an automated email, please do not reply.</p>")
            .append("</div>")
            .append("</body>")
            .append("</html>");
            
            return html.toString();
        } catch (Exception e) {
            logger.error("Error generating order status update HTML for Order #{}: {}", order.getId(), e.getMessage());
            logger.debug("Detailed error in HTML generation:", e);
            
            // Return a simplified fallback email
            return generateFallbackStatusEmail(order);
        }
    }
    
    /**
     * Generates a simple plain text-styled HTML email as a fallback
     * when the regular status update email template generation fails
     */
    private String generateFallbackStatusEmail(Order order) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html><head><title>Order Status Update</title></head><body>")
            .append("<h1>Order Status Update</h1>")
            .append("<p>Your order #").append(order.getId()).append(" status has been updated to: ").append(order.getStatus()).append("</p>")
            .append("<p>Please contact customer service if you have any questions.</p>")
            .append("</body></html>");
        return html.toString();
    }
    
    private String generateOrderConfirmationHtml(Order order) {
        logger.debug("Generating HTML for order confirmation email - Order ID: {}, Items count: {}", 
                    order.getId(), order.getItems() != null ? order.getItems().size() : 0);
        
        try {
            StringBuilder html = new StringBuilder();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            
            html.append("<!DOCTYPE html>")
            .append("<html>")
            .append("<head>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 650px; margin: 0 auto; }")
            .append(".header { background-color: #1976d2; color: white; padding: 20px; text-align: center; }")
            .append(".content { padding: 20px; }")
            .append(".order-details { margin-bottom: 30px; }")
            .append(".shipping-details { margin-bottom: 30px; }")
            .append("table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }")
            .append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }")
            .append("th { background-color: #f2f2f2; }")
            .append(".footer { background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; }")
            .append(".total-row { font-weight: bold; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class='header'>")
            .append("<h1>Thank you for your order!</h1>")
            .append("</div>")
            .append("<div class='content'>")
            
            // Order details
            .append("<div class='order-details'>")
            .append("<h2>Order Information</h2>")
            .append("<p><strong>Order Number:</strong> #").append(order.getId()).append("</p>");
            
        // Format date safely with null check
        if (order.getOrderDate() != null) {
            html.append("<p><strong>Order Date:</strong> ").append(dateFormatter.format(order.getOrderDate())).append("</p>");
        } else {
            html.append("<p><strong>Order Date:</strong> Not available</p>");
            logger.warn("Order date is null for Order #{}", order.getId());
        }
            
        html.append("<p><strong>Payment Method:</strong> ").append(order.getPaymentMethod()).append("</p>")
            .append("<p><strong>Order Status:</strong> ").append(order.getStatus()).append("</p>")
            .append("</div>")
            
            // Shipping information
            .append("<div class='shipping-details'>")
            .append("<h2>Shipping Information</h2>")
            .append("<p><strong>Name:</strong> ").append(order.getFirstName()).append(" ").append(order.getLastName()).append("</p>")
            .append("<p><strong>Address:</strong> ").append(order.getAddress()).append("</p>")
            .append("<p><strong>City:</strong> ").append(order.getCity()).append(", ").append(order.getState()).append(" ").append(order.getZipCode()).append("</p>")
            .append("<p><strong>Email:</strong> ").append(order.getEmail()).append("</p>")
            .append("<p><strong>Phone:</strong> ").append(order.getPhone()).append("</p>")
            .append("</div>")
            
            // Order items table
            .append("<h2>Order Summary</h2>")
            .append("<table>")
            .append("<tr>")
            .append("<th>Item</th>")
            .append("<th>Author</th>")
            .append("<th>Quantity</th>")
            .append("<th>Price</th>")
            .append("<th>Total</th>")
            .append("</tr>");
            
        // Add each order item
        for (OrderItem item : order.getItems()) {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            
            html.append("<tr>")
                .append("<td>").append(item.getBook().getTitle()).append("</td>")
                .append("<td>").append(item.getBook().getAuthor()).append("</td>")
                .append("<td>").append(item.getQuantity()).append("</td>")
                .append("<td>$").append(item.getPrice()).append("</td>")
                .append("<td>$").append(itemTotal).append("</td>")
                .append("</tr>");
        }
        
        // Add total
        html.append("<tr class='total-row'>")
            .append("<td colspan='4' style='text-align: right;'><strong>Total:</strong></td>")
            .append("<td>$").append(order.getTotalAmount()).append("</td>")
            .append("</tr>")
            .append("</table>")
            
            // Footer
            .append("<p>If you have any questions about your order, please contact our customer support at <a href='mailto:support@candybookstore.com'>support@candybookstore.com</a></p>")
            .append("</div>")
            .append("<div class='footer'>")
            .append("<p>&copy; 2025 Candy Book Store. All rights reserved.</p>")
            .append("<p>This is an automated email, please do not reply.</p>")
            .append("</div>")
            .append("</body>")
            .append("</html>");
            
            return html.toString();
        } catch (Exception e) {
            logger.error("Error generating order confirmation HTML for Order #{}: {}", order.getId(), e.getMessage());
            logger.debug("Detailed error in HTML generation:", e);
            
            // Return a simplified fallback email
            return generateFallbackOrderEmail(order);
        }
    }
    
    /**
     * Generates a simple plain text-styled HTML email as a fallback
     * when the regular email template generation fails
     */
    private String generateFallbackOrderEmail(Order order) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html><head><title>Order Confirmation</title></head><body>")
            .append("<h1>Order Confirmation</h1>")
            .append("<p>Thank you for your order #").append(order.getId()).append("</p>")
            .append("<p>Please contact customer service if you have any questions.</p>")
            .append("</body></html>");
        return html.toString();
    }
    
    public void sendOrderDeliveredEmail(Order order) {
        logger.info("Preparing to send order delivered confirmation email for Order #{}", order.getId());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            String recipientEmail = order.getEmail();
            logger.debug("Setting email recipient to: {}", recipientEmail);
            helper.setTo(recipientEmail);
            
            String subject = "Your Order #" + order.getId() + " Has Been Delivered - Candy Book Store";
            logger.debug("Setting email subject to: {}", subject);
            helper.setSubject(subject);
            
            // Generate HTML email content
            logger.debug("Generating HTML content for order delivered email");
            String emailContent = generateOrderDeliveredHtml(order);
            helper.setText(emailContent, true);
            
            // Log MIME message details for debugging
            logger.debug("Email MIME message details: contentType={}, encoding={}, messageId={}",
                    message.getContentType(),
                    message.getEncoding(),
                    message.getMessageID());
            
            logger.info("Sending order delivered email to {} for Order #{}", recipientEmail, order.getId());
            mailSender.send(message);
            logger.info("Successfully sent order delivered email for Order #{}", order.getId());
        } catch (MessagingException e) {
            logger.error("Failed to send order delivered email for Order #{}: {}", order.getId(), e.getMessage());
            logger.debug("Detailed error when sending delivered email:", e);
        } catch (Exception e) {
            logger.error("Unexpected error when sending order delivered email for Order #{}: {}", order.getId(), e.getMessage());
            logger.debug("Detailed unexpected error:", e);
        }
    }
    
    private String generateOrderDeliveredHtml(Order order) {
        logger.debug("Generating HTML for order delivered email - Order ID: {}", order.getId());
        
        try {
            StringBuilder html = new StringBuilder();
        
            html.append("<!DOCTYPE html>")
                .append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 650px; margin: 0 auto; }")
                .append(".header { background-color: #1976d2; color: white; padding: 20px; text-align: center; }")
                .append(".content { padding: 20px; }")
                .append(".delivery-box { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; padding: 20px; margin-bottom: 20px; text-align: center; }")
                .append(".order-summary { margin: 20px 0; padding: 20px; background-color: #f9f9f9; border: 1px solid #ddd; }")
                .append(".footer { background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; }")
                .append(".review-button { display: inline-block; background-color: #1976d2; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; margin-top: 15px; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='header'>")
                .append("<h1>Your Order Has Been Delivered!</h1>")
                .append("</div>")
                .append("<div class='content'>")
                .append("<p>Dear ").append(order.getFirstName()).append(" ").append(order.getLastName()).append(",</p>")
                
                .append("<div class='delivery-box'>")
                .append("<h2>Thank You for Confirming Delivery</h2>")
                .append("<p>Your order #").append(order.getId()).append(" has been confirmed as delivered.</p>")
                .append("<p>We hope you enjoy your books! If you have time, we'd love to hear your feedback.</p>")
                .append("<a href='http://localhost:3000/review' class='review-button'>Write a Review</a>")
                .append("</div>")
                
                .append("<div class='order-summary'>")
                .append("<h3>Order Summary</h3>")
                .append("<p><strong>Order Number:</strong> #").append(order.getId()).append("</p>")
                .append("<p><strong>Total Amount:</strong> $").append(order.getTotalAmount()).append("</p>")
                .append("<p><strong>Items:</strong> ").append(order.getItems().size()).append("</p>")
                .append("<p>To view your full order details, please <a href='http://localhost:3000/my-orders/").append(order.getId()).append("'>click here</a>.</p>")
                .append("</div>")
                
                .append("<p>Thank you for shopping with Candy Book Store. We hope to see you again soon!</p>")
                
                .append("<p>If you have any questions or concerns, please contact our customer service team at <a href='mailto:support@candybookstore.com'>support@candybookstore.com</a>.</p>")
                .append("</div>")
                .append("<div class='footer'>")
                .append("<p>&copy; 2025 Candy Book Store. All rights reserved.</p>")
                .append("<p>This is an automated email, please do not reply.</p>")
                .append("</div>")
                .append("</body>")
                .append("</html>");
            
            return html.toString();
        } catch (Exception e) {
            logger.error("Error generating order delivered HTML for Order #{}: {}", order.getId(), e.getMessage());
            logger.debug("Detailed error in HTML generation:", e);
            
            // Return a simplified fallback email
            return generateFallbackDeliveredEmail(order);
        }
    }
    
    /**
     * Generates a simple plain text-styled HTML email as a fallback
     * when the regular delivered email template generation fails
     */
    private String generateFallbackDeliveredEmail(Order order) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html><head><title>Order Delivered</title></head><body>")
            .append("<h1>Order Delivered</h1>")
            .append("<p>Your order #").append(order.getId()).append(" has been confirmed as delivered.</p>")
            .append("<p>Thank you for shopping with us!</p>")
            .append("<p>Please contact customer service if you have any questions.</p>")
            .append("</body></html>");
        return html.toString();
    }
}