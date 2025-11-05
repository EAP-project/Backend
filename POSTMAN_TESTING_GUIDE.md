# POSTMAN TESTING GUIDE - Forgot Password Feature

## 🎯 Quick Test Guide

### Endpoint 1: FORGOT PASSWORD
```
POST http://localhost:8080/api/forgot-password
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "email": "user@example.com"
}
```

**Expected Response (200 OK):**
```
"Password reset email sent successfully. Please check your inbox."
```

---

### Endpoint 2: RESET PASSWORD
```
POST http://localhost:8080/api/reset-password
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "token": "paste-token-from-email-here",
  "newPassword": "newPassword123"
}
```

**Expected Response (200 OK):**
```
"Password reset successfully! You can now log in with your new password."
```

**Error Responses:**
- **400 Bad Request:** Invalid or expired token
  ```json
  {
    "message": "Invalid password reset token"
  }
  ```
  or
  ```json
  {
    "message": "Password reset token has expired"
  }
  ```

---

## 📋 Complete Test Workflow

### Test Case 1: Successful Password Reset

**1. Request Password Reset**
```
POST http://localhost:8080/api/forgot-password

Body:
{
  "email": "vijayanga1234@gmail.com"
}

Expected: "Password reset email sent successfully. Please check your inbox."
```

**2. Check Email**
- Open your email inbox
- Find email with subject: "Password Reset Request - Automobile Service"
- Copy the token from the reset link

**4. Reset Password**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "copy-token-here",
  "newPassword": "MyNewPass123"
}

Expected: "Password reset successfully! You can now log in with your new password."
```

**5. Login with New Password**
```
POST http://localhost:8080/api/login

Body:
{
  "email": "vijayanga1234@gmail.com",
  "password": "MyNewPass123"
}

Expected: JWT token and user details
```

---

### Test Case 2: Non-Existent Email

**Request:**
```
POST http://localhost:8080/api/forgot-password

Body:
{
  "email": "nonexistent@example.com"
}

Expected: "If the email exists, a password reset link will be sent."
```

**Note:** For security, the response doesn't reveal if the email exists or not.

---

### Test Case 3: Invalid Token

**Request:**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "invalid-token-12345",
  "newPassword": "newPassword123"
}

Expected: 400 Bad Request
{
  "message": "Invalid password reset token"
}
```

---

### Test Case 4: Expired Token

**Steps:**
1. Request password reset
2. Wait for 1 hour (or manually set expiry in database)
3. Try to use the token

**Request:**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "expired-token",
  "newPassword": "newPassword123"
}

Expected: 400 Bad Request
{
  "message": "Password reset token has expired"
}
```

---

### Test Case 5: Validation Errors

**Missing Email:**
```
POST http://localhost:8080/api/forgot-password

Body:
{
  "email": ""
}

Expected: 400 Bad Request with validation error
```

**Short Password:**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "valid-token",
  "newPassword": "123"
}

Expected: 400 Bad Request
"Password must be at least 6 characters"
```

---

## 🔍 Database Verification

**Check if token was saved:**
```sql
SELECT email, password_reset_token, password_reset_token_expiry 
FROM users 
WHERE email = 'your-email@example.com';
```

**After successful reset, token should be NULL:**
```sql
SELECT email, password_reset_token, password_reset_token_expiry 
FROM users 
WHERE email = 'your-email@example.com';

-- password_reset_token should be NULL
-- password_reset_token_expiry should be NULL
```

---

## 📧 Email Configuration

Make sure email is configured in `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=syntaxterminators123@gmail.com
spring.mail.password=oxzzlnvounrsnfde
```

---

## ⚡ Quick Copy-Paste Requests

### Forgot Password (Copy-Paste Ready)
```json
POST http://localhost:8080/api/forgot-password
Content-Type: application/json

{
  "email": "vijayanga1234@gmail.com"
}
```

### Reset Password (Copy-Paste Ready)
```json
POST http://localhost:8080/api/reset-password
Content-Type: application/json

{
  "token": "PASTE_TOKEN_HERE",
  "newPassword": "NewPassword123"
}
```

### Login After Reset (Copy-Paste Ready)
```json
POST http://localhost:8080/api/login
Content-Type: application/json

{
  "email": "vijayanga1234@gmail.com",
  "password": "NewPassword123"
}
```

---

## ✅ Expected Test Results Summary

| Test Case | Endpoint | Expected Status | Expected Response |
|-----------|----------|----------------|-------------------|
| Valid email | `/forgot-password` | 200 OK | Email sent message |
| Invalid email | `/forgot-password` | 200 OK | Generic message (security) |
| Valid token + password | `/reset-password` | 200 OK | Success message |
| Invalid token | `/reset-password` | 400 Bad Request | Invalid token error |
| Expired token | `/reset-password` | 400 Bad Request | Expired token error |
| Short password | `/reset-password` | 400 Bad Request | Validation error |
| Login after reset | `/login` | 200 OK | JWT token |

---

## 🚀 Start Testing!

1. Start your Spring Boot application
2. Open Postman
3. Create a new request collection
4. Copy and paste the requests above
5. Test each scenario
6. Check your email inbox for reset links

**All endpoints are ready and tested!** ✅

---

**Created:** November 5, 2025
**Status:** Ready for Postman Testing
# POSTMAN TESTING GUIDE - Forgot Password Feature

## 🎯 Quick Test Guide

### Endpoint 1: FORGOT PASSWORD
```
POST http://localhost:8080/api/forgot-password
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "email": "user@example.com"
}
```

**Expected Response (200 OK):**
```
"Password reset email sent successfully. Please check your inbox."
```

---

### Endpoint 2: RESET PASSWORD
```
POST http://localhost:8080/api/reset-password
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "token": "paste-token-from-email-here",
  "newPassword": "newPassword123"
}
```

**Expected Response (200 OK):**
```
"Password reset successfully! You can now log in with your new password."
```

**Error Responses:**
- **400 Bad Request:** Invalid or expired token
  ```json
  {
    "message": "Invalid password reset token"
  }
  ```
  or
  ```json
  {
    "message": "Password reset token has expired"
  }
  ```

---

## 📋 Complete Test Workflow

### Test Case 1: Successful Password Reset

**1. Request Password Reset**
```
POST http://localhost:8080/api/forgot-password

Body:
{
  "email": "vijayanga1234@gmail.com"
}

Expected: "Password reset email sent successfully. Please check your inbox."
```

**2. Check Email**
- Open your email inbox
- Find email with subject: "Password Reset Request - Automobile Service"
- **Option A:** Click the verification link in the email (opens browser)
  - Browser shows: "Token is valid. Token: xyz-123-abc"
  - Copy the token from the response
- **Option B:** Copy the token directly from the email body

**3. Validate Token (Optional - if you clicked the link)**
```
GET http://localhost:8080/api/reset-password?token={token-from-email}

Expected: "Token is valid. Please use POST /api/reset-password with your new password. Token: {token}"
```

**4. Reset Password**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "copy-token-here",
  "newPassword": "MyNewPass123"
}

Expected: "Password reset successfully! You can now log in with your new password."
```

**4. Login with New Password**
```
POST http://localhost:8080/api/login

Body:
{
  "email": "vijayanga1234@gmail.com",
  "password": "MyNewPass123"
}

Expected: JWT token and user details
```

---

### Test Case 2: Non-Existent Email

**Request:**
```
POST http://localhost:8080/api/forgot-password

Body:
{
  "email": "nonexistent@example.com"
}

Expected: "If the email exists, a password reset link will be sent."
```

**Note:** For security, the response doesn't reveal if the email exists or not.

---

### Test Case 3: Invalid Token

**Request:**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "invalid-token-12345",
  "newPassword": "newPassword123"
}

Expected: 400 Bad Request
{
  "message": "Invalid password reset token"
}
```

---

### Test Case 4: Expired Token

**Steps:**
1. Request password reset
2. Wait for 1 hour (or manually set expiry in database)
3. Try to use the token

**Request:**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "expired-token",
  "newPassword": "newPassword123"
}

Expected: 400 Bad Request
{
  "message": "Password reset token has expired"
}
```

---

### Test Case 5: Validation Errors

**Missing Email:**
```
POST http://localhost:8080/api/forgot-password

Body:
{
  "email": ""
}

Expected: 400 Bad Request with validation error
```

**Short Password:**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "valid-token",
  "newPassword": "123"
}

Expected: 400 Bad Request
"Password must be at least 6 characters"
```

---

## 🔍 Database Verification

**Check if token was saved:**
```sql
SELECT email, password_reset_token, password_reset_token_expiry 
FROM users 
WHERE email = 'your-email@example.com';
```

**After successful reset, token should be NULL:**
```sql
SELECT email, password_reset_token, password_reset_token_expiry 
FROM users 
WHERE email = 'your-email@example.com';

-- password_reset_token should be NULL
-- password_reset_token_expiry should be NULL
```

---

## 📧 Email Configuration

Make sure email is configured in `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=syntaxterminators123@gmail.com
spring.mail.password=oxzzlnvounrsnfde
```

---

## ⚡ Quick Copy-Paste Requests

### Forgot Password (Copy-Paste Ready)
```json
POST http://localhost:8080/api/forgot-password
Content-Type: application/json

{
  "email": "vijayanga1234@gmail.com"
}
```

### Reset Password (Copy-Paste Ready)
```json
POST http://localhost:8080/api/reset-password
Content-Type: application/json

{
  "token": "PASTE_TOKEN_HERE",
  "newPassword": "NewPassword123"
}
```

### Login After Reset (Copy-Paste Ready)
```json
POST http://localhost:8080/api/login
Content-Type: application/json

{
  "email": "vijayanga1234@gmail.com",
  "password": "NewPassword123"
}
```

---

## ✅ Expected Test Results Summary

| Test Case | Endpoint | Expected Status | Expected Response |
|-----------|----------|----------------|-------------------|
| Valid email | `/forgot-password` | 200 OK | Email sent message |
| Invalid email | `/forgot-password` | 200 OK | Generic message (security) |
| Valid token + password | `/reset-password` | 200 OK | Success message |
| Invalid token | `/reset-password` | 400 Bad Request | Invalid token error |
| Expired token | `/reset-password` | 400 Bad Request | Expired token error |
| Short password | `/reset-password` | 400 Bad Request | Validation error |
| Login after reset | `/login` | 200 OK | JWT token |

---

## 🚀 Start Testing!

1. Start your Spring Boot application
2. Open Postman
3. Create a new request collection
4. Copy and paste the requests above
5. Test each scenario
6. Check your email inbox for reset links

**All endpoints are ready and tested!** ✅

---

**Created:** November 5, 2025
**Status:** Ready for Postman Testing
# POSTMAN TESTING GUIDE - Forgot Password Feature

## 🎯 Quick Test Guide

### Endpoint 1: FORGOT PASSWORD
```
POST http://localhost:8080/api/forgot-password
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "email": "user@example.com"
}
```

**Expected Response (200 OK):**
```
"Password reset email sent successfully. Please check your inbox."
```

---

### Endpoint 2: VALIDATE RESET TOKEN (Email Link)
```
GET http://localhost:8080/api/reset-password?token={your-token}
```

**Purpose:** This endpoint is called when user clicks the link in the email

**Expected Response (200 OK):**
```
"Token is valid. Please use POST /api/reset-password with your new password. Token: {your-token}"
```

**What to do:**
1. Click the link in your email
2. Browser opens showing the token is valid
3. Copy the token from the response
4. Use it in the POST endpoint below

**Error Responses:**
- **400 Bad Request:** Invalid or expired token

---

### Endpoint 3: RESET PASSWORD
```
POST http://localhost:8080/api/reset-password
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "token": "paste-token-from-email-here",
  "newPassword": "newPassword123"
}
```

**Expected Response (200 OK):**
```
"Password reset successfully! You can now log in with your new password."
```

**Error Responses:**
- **400 Bad Request:** Invalid or expired token
  ```json
  {
    "message": "Invalid password reset token"
  }
  ```
  or
  ```json
  {
    "message": "Password reset token has expired"
  }
  ```

---

## 📋 Complete Test Workflow

### Test Case 1: Successful Password Reset

**1. Request Password Reset**
```
POST http://localhost:8080/api/forgot-password

Body:
{
  "email": "vijayanga1234@gmail.com"
}

Expected: "Password reset email sent successfully. Please check your inbox."
```

**2. Check Email**
- Open your email inbox
- Find email with subject: "Password Reset Request - Automobile Service"
- Copy the token from the reset link

**3. Reset Password**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "copy-token-here",
  "newPassword": "MyNewPass123"
}

Expected: "Password reset successfully! You can now log in with your new password."
```

**4. Login with New Password**
```
POST http://localhost:8080/api/login

Body:
{
  "email": "vijayanga1234@gmail.com",
  "password": "MyNewPass123"
}

Expected: JWT token and user details
```

---

### Test Case 2: Non-Existent Email

**Request:**
```
POST http://localhost:8080/api/forgot-password

Body:
{
  "email": "nonexistent@example.com"
}

Expected: "If the email exists, a password reset link will be sent."
```

**Note:** For security, the response doesn't reveal if the email exists or not.

---

### Test Case 3: Invalid Token

**Request:**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "invalid-token-12345",
  "newPassword": "newPassword123"
}

Expected: 400 Bad Request
{
  "message": "Invalid password reset token"
}
```

---

### Test Case 4: Expired Token

**Steps:**
1. Request password reset
2. Wait for 1 hour (or manually set expiry in database)
3. Try to use the token

**Request:**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "expired-token",
  "newPassword": "newPassword123"
}

Expected: 400 Bad Request
{
  "message": "Password reset token has expired"
}
```

---

### Test Case 5: Validation Errors

**Missing Email:**
```
POST http://localhost:8080/api/forgot-password

Body:
{
  "email": ""
}

Expected: 400 Bad Request with validation error
```

**Short Password:**
```
POST http://localhost:8080/api/reset-password

Body:
{
  "token": "valid-token",
  "newPassword": "123"
}

Expected: 400 Bad Request
"Password must be at least 6 characters"
```

---

## 🔍 Database Verification

**Check if token was saved:**
```sql
SELECT email, password_reset_token, password_reset_token_expiry 
FROM users 
WHERE email = 'your-email@example.com';
```

**After successful reset, token should be NULL:**
```sql
SELECT email, password_reset_token, password_reset_token_expiry 
FROM users 
WHERE email = 'your-email@example.com';

-- password_reset_token should be NULL
-- password_reset_token_expiry should be NULL
```

---

## 📧 Email Configuration

Make sure email is configured in `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=syntaxterminators123@gmail.com
spring.mail.password=oxzzlnvounrsnfde
```

---

## ⚡ Quick Copy-Paste Requests

### Forgot Password (Copy-Paste Ready)
```json
POST http://localhost:8080/api/forgot-password
Content-Type: application/json

{
  "email": "vijayanga1234@gmail.com"
}
```

### Reset Password (Copy-Paste Ready)
```json
POST http://localhost:8080/api/reset-password
Content-Type: application/json

{
  "token": "PASTE_TOKEN_HERE",
  "newPassword": "NewPassword123"
}
```

### Login After Reset (Copy-Paste Ready)
```json
POST http://localhost:8080/api/login
Content-Type: application/json

{
  "email": "vijayanga1234@gmail.com",
  "password": "NewPassword123"
}
```

---

## ✅ Expected Test Results Summary

| Test Case | Endpoint | Expected Status | Expected Response |
|-----------|----------|----------------|-------------------|
| Valid email | `/forgot-password` | 200 OK | Email sent message |
| Invalid email | `/forgot-password` | 200 OK | Generic message (security) |
| Valid token + password | `/reset-password` | 200 OK | Success message |
| Invalid token | `/reset-password` | 400 Bad Request | Invalid token error |
| Expired token | `/reset-password` | 400 Bad Request | Expired token error |
| Short password | `/reset-password` | 400 Bad Request | Validation error |
| Login after reset | `/login` | 200 OK | JWT token |

---

## 🚀 Start Testing!

1. Start your Spring Boot application
2. Open Postman
3. Create a new request collection
4. Copy and paste the requests above
5. Test each scenario
6. Check your email inbox for reset links

**All endpoints are ready and tested!** ✅

---

**Created:** November 5, 2025
**Status:** Ready for Postman Testing

