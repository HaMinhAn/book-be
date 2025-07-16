#!/bin/bash

# Replace these with actual values from your system
ORDER_ID=1
JWT_TOKEN="your_jwt_token_here"  # Get this by logging in

# Test the confirm-received endpoint
echo "Testing confirm-received endpoint for order $ORDER_ID"
curl -X POST "http://localhost:8080/api/orders/$ORDER_ID/confirm-received" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -v
