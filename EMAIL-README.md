# Email Configuration for Candy Book Store

The application supports sending email notifications to users for order confirmations and status updates.

## Production Configuration

For production, edit the `application.yml` file or set the following environment variables:

```
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
```

### Using Gmail

If using Gmail, you need to:

1. Enable 2-Step Verification on your Google account
2. Create an App Password at https://myaccount.google.com/apppasswords
3. Use that App Password in the EMAIL_PASSWORD environment variable

## Development Configuration

For development, we recommend using a test email service like Mailtrap.io:

1. Sign up for a free account at Mailtrap.io
2. Get your SMTP credentials from the inbox settings
3. Edit `application-dev.yml` or set the following environment variables:

```
EMAIL_DEV_HOST=smtp.mailtrap.io
EMAIL_DEV_PORT=2525
EMAIL_DEV_USERNAME=your-mailtrap-username
EMAIL_DEV_PASSWORD=your-mailtrap-password
```

## Email Templates

The application uses HTML email templates for:

1. Order Confirmation - Sent when a user completes a purchase
2. Order Status Updates - Sent when an order status changes (Processing, Shipped, Delivered, etc.)

You can customize these templates in the `EmailService.java` file.

## Testing Email Sending

To test email sending functionality:

1. Configure the appropriate SMTP settings for your environment
2. Place a test order in the system
3. Check your email inbox (or Mailtrap inbox for development)

For any issues, check the application logs for detailed error messages.
