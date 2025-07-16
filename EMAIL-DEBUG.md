# Email Debugging Guide

This document explains how to debug email sending functionality in the Candy Book Store application.

## Logging Configuration

Email-related logging is configured at multiple levels to provide comprehensive insights:

### Production Configuration (`application.yml`)

```yaml
logging:
  level:
    root: INFO
    com.example.candy.services.EmailService: DEBUG
    com.example.candy.services.OrderService: DEBUG
    org.springframework.mail: DEBUG
    com.sun.mail: DEBUG
```

### Development Configuration (`application-dev.yml`)

```yaml
logging:
  level:
    root: INFO
    com.example.candy.services.EmailService: DEBUG
    com.example.candy.services.OrderService: DEBUG
    org.springframework.mail: DEBUG
    com.sun.mail: DEBUG
```

## Debug Endpoints

The application provides a special admin endpoint for testing email connectivity:

```
GET /api/admin/debug/email-connection
```

This endpoint requires admin authorization and will:

1. Log detailed SMTP server configuration
2. Attempt to connect to the mail server
3. Log the result of the connection attempt

Check the application logs after calling this endpoint to see detailed diagnostic information.

## Debugging Process

When debugging email functionality, follow these steps:

1. **Check Application Logs**: Look for logs from the following components:

   - `EmailService`: For application-level email operations
   - `OrderService`: For the process of triggering emails during order operations
   - `org.springframework.mail`: For Spring's mail sending infrastructure
   - `com.sun.mail`: For low-level Java Mail operations

2. **Types of Logs to Look For**:

   - `INFO` level logs: Normal operation events like "Attempting to send email", "Email sent successfully"
   - `DEBUG` level logs: Detailed information about email contents, connection details
   - `ERROR` level logs: Failed email sending operations with error messages

3. **Common Issues and Debug Information**:
   - Authentication failures: Check username/password in logs
   - Connection issues: Look for connection timeout or refused messages
   - Template rendering errors: Check for errors in HTML generation
   - SMTP configuration issues: Verify host, port, and TLS settings

## Testing Email Functionality

For testing email functionality without sending real emails:

1. **Development Environment**:

   - Configure Mailtrap or similar service in `application-dev.yml`
   - Check Mailtrap inbox to verify email content and delivery

2. **Manual Testing**:
   - Create an order through the API/frontend
   - Check logs for email sending information
   - Verify the email content in the test inbox

## Environment Variables

The following environment variables can be adjusted for email configuration:

```
EMAIL_HOST=smtp.example.com
EMAIL_PORT=587
EMAIL_USERNAME=your-username
EMAIL_PASSWORD=your-password
```

For development testing:

```
EMAIL_DEV_HOST=smtp.mailtrap.io
EMAIL_DEV_PORT=2525
EMAIL_DEV_USERNAME=your-mailtrap-username
EMAIL_DEV_PASSWORD=your-mailtrap-password
```

## Troubleshooting Common Issues

1. **No emails are being sent, no errors in logs**:

   - Check if email service is properly autowired
   - Verify that email sending methods are being called

2. **Authentication errors**:

   - Verify credentials are correct
   - For Gmail, ensure "Less secure app access" is enabled or use app password

3. **Connection timeouts**:

   - Check firewall settings
   - Verify SMTP server hostname and port

4. **Template rendering issues**:
   - Check for null values in the order object
   - Verify HTML generation logic
5. **Date formatting issues**:
   - The system uses LocalDateTime for dates but requires proper formatting
   - If you see "Cannot format given Object as a Date" errors, ensure proper handling of LocalDateTime objects
   - Check that DateTimeFormatter is used correctly with LocalDateTime objects
