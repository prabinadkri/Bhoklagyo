# Admin System Setup Guide

## Overview
The Bhoklagyo system now uses a separate Admin entity for platform management. Only admins can:
- Create restaurants
- Assign owners to restaurants

## Database Schema
A new `admins` table has been created separate from the `users` table.

## Initial Admin Setup

### Option 1: Using Thunder Client
1. Use the "Register Admin" endpoint in Thunder Collection
2. POST to `http://localhost:8080/admin/register`
3. Body:
```json
{
  "username": "admin1",
  "name": "System Admin",
  "password": "admin123",
  "email": "admin@bhoklagyo.com",
  "phoneNumber": "+977-9800000000"
}
```

### Option 2: Direct Database Insert
Run this SQL in your PostgreSQL database:
```sql
INSERT INTO admins (username, name, password, email, phone_number)
VALUES (
  'admin1',
  'System Admin',
  '$2a$10$YourBcryptHashedPasswordHere',  -- Use BCrypt to hash your password
  'admin@bhoklagyo.com',
  '+977-9800000000'
);
```

## Workflow

### 1. Admin Login
- Endpoint: `POST /admin/login`
- Get JWT token with ADMIN role

### 2. Create Restaurant
- Endpoint: `POST /restaurants` (Admin Only)
- Use admin JWT token
- Restaurant is created without an owner initially

### 3. Register Owner User
- Endpoint: `POST /auth/register`
- Create a user with OWNER role

### 4. Assign Owner to Restaurant
- Endpoint: `POST /admin/assign-owner` (Admin Only)
- Body:
```json
{
  "userId": 1,
  "restaurantId": 1
}
```

## Authorization Matrix

| Action | Admin | Owner | Employee | Customer |
|--------|-------|-------|----------|----------|
| Create Restaurant | ✓ | ✗ | ✗ | ✗ |
| Assign Owner | ✓ | ✗ | ✗ | ✗ |
| Add/Update/Delete Menu | ✗ | ✓ | ✗ | ✗ |
| Update Order Status | ✗ | ✓ | ✓ | ✗ |
| Create Order | ✗ | ✗ | ✗ | ✓ |
| View Own Orders | ✗ | ✗ | ✗ | ✓ |
| View Restaurant Orders | ✗ | ✓ | ✓ | ✗ |

## API Endpoints

### Admin Endpoints
- `POST /admin/register` - Register new admin (public)
- `POST /admin/login` - Admin login (public)
- `POST /admin/assign-owner` - Assign owner to restaurant (admin only)

### User Endpoints (unchanged)
- `POST /auth/register` - Register customer/owner/employee
- `POST /auth/login` - User login

### Restaurant Endpoints (modified)
- `POST /restaurants` - Create restaurant (admin only)
- `GET /restaurants` - List restaurants (public)
- `GET /restaurants/{id}` - Get restaurant (public)
