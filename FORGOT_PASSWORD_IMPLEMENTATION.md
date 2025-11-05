# FORGOT PASSWORD FEATURE - Implementation Guide

## ✅ Feature Added Successfully

All changes are marked with comments: `// ADDED FOR FORGOT PASSWORD FEATURE`

---

## 📋 Files Modified/Created

### 1. **User.java** - Added Password Reset Fields
**Location:** `src/main/java/com/automobileproject/EAP/model/User.java`

**New Fields:**
```java
// Password reset token - ADDED FOR FORGOT PASSWORD FEATURE
private String passwordResetToken;

// Password reset token expiry - ADDED FOR FORGOT PASSWORD FEATURE
private java.time.LocalDateTime passwordResetTokenExpiry;
```

---

### 2. **EmailService.java** - Added Password Reset Email Method
**Location:** `src/main/java/com/automobileproject/EAP/service/EmailService.java`

**New Method:**
```java
// Send password reset email - ADDED FOR FORGOT PASSWORD FEATURE
public void sendPasswordResetEmail(String toEmail, String resetToken)
```

**What it does:**
- Sends email with password reset link
- Link includes unique token
- Token expires in 1 hour
- Email contains user-friendly instructions

---

### 3. **UserService.java** - Added Password Reset Methods
**Location:** `src/main/java/com/automobileproject/EAP/service/UserService.java`

**New Methods:**

**A. Request Password Reset:**
```java
// Request password reset - ADDED FOR FORGOT PASSWORD FEATURE
@Transactional
public void requestPasswordReset(String email)
```
- Generates unique reset token (UUID)
- Sets token expiry to 1 hour
- Sends password reset email
- Saves token to database

**B. Reset Password:**
```java
// Reset password with token - ADDED FOR FORGOT PASSWORD FEATURE
@Transactional
public void resetPassword(String token, String newPassword)
```
- Validates reset token
- Checks if token expired
- Updates password with encryption
- Clears reset token after use

---

### 4. **UserRepository.java** - Added Token Lookup
**Location:** `src/main/java/com/automobileproject/EAP/repository/UserRepository.java`

**New Method:**
```java
// Find user by password reset token - ADDED FOR FORGOT PASSWORD FEATURE
Optional<User> findByPasswordResetToken(String passwordResetToken);
```

---

### 5. **ForgotPasswordRequest.java** - NEW DTO
**Location:** `src/main/java/com/automobileproject/EAP/dto/ForgotPasswordRequest.java`

**Fields:**
```java
@NotBlank(message = "Email is required")
@Email(message = "Email should be valid")
private String email;
```

---

### 6. **ResetPasswordRequest.java** - NEW DTO
**Location:** `src/main/java/com/automobileproject/EAP/dto/ResetPasswordRequest.java`

**Fields:**
```java
@NotBlank(message = "Token is required")
private String token;

@NotBlank(message = "New password is required")
@Size(min = 6, message = "Password must be at least 6 characters")
private String newPassword;
```

---

### 7. **AuthController.java** - Added Password Reset Endpoints
**Location:** `src/main/java/com/automobileproject/EAP/controller/AuthController.java`

**New Endpoints:**

**A. Forgot Password Endpoint:**
```java
// Forgot password endpoint - ADDED FOR FORGOT PASSWORD FEATURE
@PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request)
```

**B. Reset Password Endpoint:**
```java
// Reset password endpoint - ADDED FOR FORGOT PASSWORD FEATURE
@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request)
```

---

### 8. **SecurityConfig.java** - Added Public Access
**Location:** `src/main/java/com/automobileproject/EAP/config/SecurityConfig.java`

**Modified Line:**
```java
// Public endpoints - MODIFIED FOR EMAIL VERIFICATION AND FORGOT PASSWORD
.requestMatchers("/api/register", "/api/login", "/api/verify-email", "/api/forgot-password", "/api/reset-password").permitAll()
```

---

## 🔄 How It Works

### **Flow 1: Request Password Reset**
1. User forgets password
2. User sends POST to `/api/forgot-password` with email
3. System generates unique token (UUID)
4. Token stored in database with 1-hour expiry
5. Password reset email sent to user
6. Response sent (doesn't reveal if email exists for security)

### **Flow 2: Reset Password**
1. User clicks link in email
2. User sends POST to `/api/reset-password` with token and new password
3. System validates token
4. System checks if token expired
5. Password updated and encrypted
6. Token cleared from database
7. Success response sent
8. User can now login with new password

---

## 📨 API Endpoints for Postman

### **1. Forgot Password**
```
POST http://localhost:8080/api/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Success Response:**
```json
"Password reset email sent successfully. Please check your inbox."
```

**Note:** Always returns success even if email doesn't exist (security feature)

---

### **2. Reset Password**
```
POST http://localhost:8080/api/reset-password
Content-Type: application/json

{
  "token": "your-reset-token-from-email",
  "newPassword": "newPassword123"
}
```

**Success Response:**
```json
"Password reset successfully! You can now log in with your new password."
```

**Error Responses:**
- Invalid token: `"Invalid password reset token"`
- Expired token: `"Password reset token has expired"`
- Validation errors: Field-specific error messages

---

## 🗄️ Database Changes

**New Columns in `users` Table:**
- `password_reset_token` (varchar) - Stores the reset token
- `password_reset_token_expiry` (timestamp) - Token expiration time

These will be automatically created when you run the application.

---

## 🔐 Security Features

1. **Token Expiry:** Reset tokens expire after 1 hour
2. **One-Time Use:** Token is cleared after password reset
3. **Email Privacy:** Doesn't reveal if email exists in forgot-password response
4. **Encrypted Storage:** Passwords are bcrypt encrypted
5. **UUID Tokens:** Cryptographically random tokens (hard to guess)

---

## ✅ Build Status

```
[INFO] BUILD SUCCESS
[INFO] Total time:  7.189 s
[INFO] Finished at: 2025-11-05T12:15:35+05:30
```

✅ **All changes compiled successfully**
✅ **No errors**
✅ **Ready for testing in Postman**

---

## 📧 Email Content

**Subject:** "Password Reset Request - Automobile Service"

**Body:**
```
Dear User,

We received a request to reset your password for your Automobile Service account.

Please click the link below to reset your password:
http://localhost:8080/api/reset-password?token={your-token}

This link will expire in 1 hour.

If you did not request a password reset, please ignore this email. 
Your password will remain unchanged.

Best regards,
Automobile Service Team
```

---

## 🎯 Testing Workflow in Postman

### **Step 1: Register a User** (if not already registered)
```
POST /api/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "oldPassword123",
  "firstName": "Test",
  "lastName": "User",
  "phoneNumber": "1234567890",
  "role": "CUSTOMER"
}
```

### **Step 2: Verify Email**
Check your inbox and click verification link or:
```
GET /api/verify-email?token={verification-token}
```

### **Step 3: Request Password Reset**
```
POST /api/forgot-password
{
  "email": "test@example.com"
}
```
✅ Check your inbox for password reset email

### **Step 4: Reset Password**
Copy the token from the email and:
```
POST /api/reset-password
{
  "token": "{token-from-email}",
  "newPassword": "newPassword123"
}
```

### **Step 5: Login with New Password**
```
POST /api/login
{
  "email": "test@example.com",
  "password": "newPassword123"
}
```
✅ Should succeed with JWT token

---

## 📝 Summary of Changes

**Files Modified:** 5
- User.java
- EmailService.java
- UserService.java
- UserRepository.java
- AuthController.java
- SecurityConfig.java

**Files Created:** 2
- ForgotPasswordRequest.java (NEW DTO)
- ResetPasswordRequest.java (NEW DTO)

**New API Endpoints:** 2
- POST `/api/forgot-password`
- POST `/api/reset-password`

**Database Fields:** 2
- `password_reset_token`
- `password_reset_token_expiry`

**Total Lines Added:** ~150 lines of code

---

## 🚀 Ready to Test!

Your forgot password feature is fully implemented and ready to test in Postman!

**All changes are clearly marked with comments for easy identification.**

---

**Last Updated:** November 5, 2025, 12:15 PM IST
**Build Status:** ✅ SUCCESS

