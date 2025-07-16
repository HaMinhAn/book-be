# Test Order Confirmation API

This PowerShell script tests the new confirm-received API endpoint.

```powershell
# Replace these with actual values from your system
$orderId = 1
$jwtToken = "your_jwt_token_here"  # Get this by logging in

# Test the confirm-received endpoint
Write-Host "Testing confirm-received endpoint for order $orderId"
Invoke-RestMethod -Method POST -Uri "http://localhost:8080/api/orders/$orderId/confirm-received" `
  -Headers @{
    "Authorization" = "Bearer $jwtToken"
    "Content-Type" = "application/json"
  } -Verbose
```

## Instructions

1. Update the `$orderId` variable with a valid order ID from your system
2. Update the `$jwtToken` variable with a valid JWT token (can be obtained by logging in)
3. Run the script in PowerShell
4. Check the response to verify the API works correctly
