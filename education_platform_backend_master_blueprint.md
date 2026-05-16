# Education Consultation Platform — Backend Master Blueprint

# 1. Project Overview

This document contains the complete backend blueprint for the MVP Phase-1 monolithic application.

Modules Included:

- Authentication
- User Management
- Expert Profile
- Expertise Management
- Availability Slots
- Booking
- Payments
- Reviews
- Session Management
- Search
- Common Utilities
- Global Exception Handling
- Security Configuration
- JWT Authentication
- API Documentation
- Validation
- Auditing

---

# 2. Recommended Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Build Tool | Gradle |
| ORM | Spring Data JPA |
| Database | PostgreSQL |
| Security | Spring Security + JWT |
| Validation | Hibernate Validator |
| Documentation | Swagger/OpenAPI |
| Migration | Flyway |
| Lombok | Yes |
| API Testing | Postman |
| Containerization | Docker |

---

# 3. Project Structure

```text
src/main/java/com/educationplatform
│
├── auth
│   ├── controller
│   ├── service
│   ├── dto
│   ├── security
│   └── util
│
├── user
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   └── mapper
│
├── expert
├── expertise
├── booking
├── payment
├── review
├── session
├── availability
├── search
│
├── common
│   ├── exception
│   ├── response
│   ├── constants
│   ├── util
│   └── config
│
└── config
```

---

# 4. Database Entities

# 4.1 USER ENTITY

## Fields

| Field | Type | Validation |
|---|---|---|
| id | UUID | Auto Generated |
| firstName | String | NotBlank |
| lastName | String | Optional |
| email | String | Email + Unique |
| password | String | Min 8 chars |
| phoneNumber | String | Regex |
| role | Role | NotNull |
| status | UserStatus | NotNull |
| emailVerified | boolean | Default false |
| profilePictureUrl | String | Optional |
| lastLoginAt | LocalDateTime | Optional |
| createdAt | LocalDateTime | Auto |
| updatedAt | LocalDateTime | Auto |

## Relationships

- ManyToOne -> Role
- OneToOne -> ExpertProfile
- OneToMany -> Booking
- OneToMany -> Review

---

# 4.2 ROLE ENTITY

| Field | Type |
|---|---|
| id | UUID |
| name | String |

## Roles

- ROLE_USER
- ROLE_EXPERT
- ROLE_ADMIN

---

# 4.3 EXPERT_PROFILE ENTITY

| Field | Type | Validation |
|---|---|---|
| id | UUID | Auto |
| user | User | NotNull |
| headline | String | NotBlank |
| bio | String | NotBlank |
| experienceYears | Integer | Min 0 |
| hourlyRate | BigDecimal | Positive |
| avgRating | Double | Default 0 |
| totalSessions | Integer | Default 0 |
| verificationStatus | Enum | NotNull |
| linkedinUrl | String | Optional |
| createdAt | LocalDateTime | Auto |
| updatedAt | LocalDateTime | Auto |

## Relationships

- OneToOne -> User
- ManyToMany -> Expertise
- OneToMany -> AvailabilitySlot
- OneToMany -> Booking
- OneToMany -> Review

---

# 4.4 EXPERTISE ENTITY

| Field | Type |
|---|---|
| id | UUID |
| name | String |
| category | String |

Examples:
- Java
- Spring Boot
- AWS
- Career Guidance
- Data Structures

---

# 4.5 AVAILABILITY_SLOT ENTITY

| Field | Type |
|---|---|
| id | UUID |
| expertProfile | ExpertProfile |
| startTime | LocalDateTime |
| endTime | LocalDateTime |
| timezone | String |
| booked | boolean |
| createdAt | LocalDateTime |

---

# 4.6 BOOKING ENTITY

| Field | Type |
|---|---|
| id | UUID |
| user | User |
| expertProfile | ExpertProfile |
| slot | AvailabilitySlot |
| bookingStatus | Enum |
| paymentStatus | Enum |
| amount | BigDecimal |
| bookingTime | LocalDateTime |
| createdAt | LocalDateTime |

---

# 4.7 PAYMENT ENTITY

| Field | Type |
|---|---|
| id | UUID |
| booking | Booking |
| paymentGateway | String |
| transactionId | String |
| amount | BigDecimal |
| currency | String |
| paymentStatus | Enum |
| paidAt | LocalDateTime |

---

# 4.8 REVIEW ENTITY

| Field | Type |
|---|---|
| id | UUID |
| booking | Booking |
| reviewer | User |
| expertProfile | ExpertProfile |
| rating | Integer |
| comment | String |
| createdAt | LocalDateTime |

Validation:
- rating between 1-5

---

# 4.9 SESSION ENTITY

| Field | Type |
|---|---|
| id | UUID |
| booking | Booking |
| meetingLink | String |
| sessionStatus | Enum |
| startTime | LocalDateTime |
| endTime | LocalDateTime |
| recordingUrl | String |

---

# 5. ENUMS

# UserStatus

```java
ACTIVE
INACTIVE
BLOCKED
DELETED
```

# VerificationStatus

```java
PENDING
VERIFIED
REJECTED
```

# BookingStatus

```java
PENDING
CONFIRMED
CANCELLED
COMPLETED
FAILED
```

# PaymentStatus

```java
INITIATED
SUCCESS
FAILED
REFUNDED
```

# SessionStatus

```java
SCHEDULED
ONGOING
COMPLETED
CANCELLED
```

---

# 6. AUTHENTICATION MODULE

# Features

- User Registration
- Login
- JWT Token Generation
- Refresh Token
- Role Based Access
- Password Encryption

---

# JWT FLOW

```text
Client -> Login API
       -> Validate Credentials
       -> Generate JWT
       -> Return Access Token
```

---

# Security Configuration

## Required Classes

- JwtAuthenticationFilter
- JwtTokenProvider
- SecurityConfig
- CustomUserDetailsService
- AuthEntryPointJwt

---

# Security Rules

| Endpoint | Access |
|---|---|
| /auth/** | Public |
| /swagger-ui/** | Public |
| /experts/search | Public |
| /admin/** | ADMIN |
| /bookings/** | USER/EXPERT |

---

# Password Encryption

Use:

```java
BCryptPasswordEncoder
```

---

# 7. CONTROLLERS

# 7.1 AUTH CONTROLLER

Base URL:

```text
/api/v1/auth
```

## Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | /register | Register User |
| POST | /login | Login |
| POST | /refresh | Refresh Token |
| POST | /logout | Logout |

---

# 7.2 USER CONTROLLER

Base URL:

```text
/api/v1/users
```

## Endpoints

| Method | Endpoint |
|---|---|
| GET | /me |
| PUT | /me |
| DELETE | /me |
| GET | /{id} |

---

# 7.3 EXPERT CONTROLLER

Base URL:

```text
/api/v1/experts
```

## Endpoints

| Method | Endpoint |
|---|---|
| POST | /profile |
| PUT | /profile |
| GET | /{id} |
| GET | /search |
| GET | /top-rated |
| GET | /availability/{expertId} |

---

# 7.4 AVAILABILITY CONTROLLER

Base URL:

```text
/api/v1/availability
```

## Endpoints

| Method | Endpoint |
|---|---|
| POST | /slots |
| PUT | /slots/{id} |
| DELETE | /slots/{id} |
| GET | /expert/{expertId} |

---

# 7.5 BOOKING CONTROLLER

Base URL:

```text
/api/v1/bookings
```

## Endpoints

| Method | Endpoint |
|---|---|
| POST | / |
| GET | /{id} |
| GET | /my-bookings |
| PUT | /cancel/{id} |
| PUT | /confirm/{id} |

---

# 7.6 PAYMENT CONTROLLER

Base URL:

```text
/api/v1/payments
```

## Endpoints

| Method | Endpoint |
|---|---|
| POST | /initiate |
| POST | /verify |
| POST | /webhook |
| GET | /{bookingId} |

---

# 7.7 REVIEW CONTROLLER

Base URL:

```text
/api/v1/reviews
```

## Endpoints

| Method | Endpoint |
|---|---|
| POST | / |
| GET | /expert/{expertId} |
| DELETE | /{id} |

---

# 8. SERVICE LAYER

# 8.1 AuthService

Responsibilities:

- Register User
- Login User
- Generate JWT
- Validate Credentials
- Refresh Token

---

# 8.2 UserService

Responsibilities:

- Update User Profile
- Fetch User
- Soft Delete User

---

# 8.3 ExpertService

Responsibilities:

- Create Expert Profile
- Update Expert Profile
- Search Experts
- Manage Skills
- Calculate Ratings

---

# 8.4 AvailabilityService

Responsibilities:

- Create Slots
- Validate Overlapping Slots
- Fetch Available Slots

---

# 8.5 BookingService

Responsibilities:

- Create Booking
- Validate Slot Availability
- Prevent Double Booking
- Cancel Booking
- Confirm Booking

---

# 8.6 PaymentService

Responsibilities:

- Initiate Payment
- Verify Payment
- Update Payment Status
- Handle Refunds

---

# 8.7 ReviewService

Responsibilities:

- Add Review
- Update Average Rating
- Fetch Reviews

---

# 8.8 SessionService

Responsibilities:

- Create Meeting Link
- Start Session
- End Session
- Save Recording URL

---

# 9. REPOSITORY LAYER

# UserRepository

```java
Optional<User> findByEmail(String email);
boolean existsByEmail(String email);
```

---

# ExpertRepository

```java
Page<ExpertProfile> findByVerificationStatus();
```

---

# BookingRepository

```java
List<Booking> findByUserId(UUID userId);
List<Booking> findByExpertProfileId(UUID expertId);
```

---

# ReviewRepository

```java
List<Review> findByExpertProfileId(UUID expertId);
```

---

# AvailabilityRepository

```java
List<AvailabilitySlot> findByExpertProfileIdAndBookedFalse();
```

---

# 10. DTOS

# Request DTOs

- RegisterRequest
- LoginRequest
- CreateExpertProfileRequest
- CreateBookingRequest
- PaymentRequest
- ReviewRequest

---

# Response DTOs

- AuthResponse
- UserResponse
- ExpertProfileResponse
- BookingResponse
- PaymentResponse
- ReviewResponse

---

# 11. VALIDATIONS

# User Validations

| Field | Validation |
|---|---|
| firstName | @NotBlank |
| email | @Email |
| password | @Size(min=8) |
| phone | Regex |

---

# Booking Validations

| Rule |
|---|
| Slot should not already be booked |
| User cannot book own session |
| Booking amount should be positive |

---

# Review Validations

| Rule |
|---|
| Rating between 1-5 |
| One review per booking |
| Review only after completed session |

---

# 12. GLOBAL EXCEPTION HANDLING

# Required Exceptions

- ResourceNotFoundException
- BadRequestException
- UnauthorizedException
- ForbiddenException
- DuplicateResourceException
- BookingConflictException

---

# GlobalExceptionHandler

Use:

```java
@RestControllerAdvice
```

Handle:
- Validation errors
- Illegal arguments
- Authentication failures
- Business exceptions

---

# 13. COMMON RESPONSE FORMAT

```json
{
  "success": true,
  "message": "Booking created successfully",
  "data": {},
  "timestamp": "2026-05-16T10:00:00"
}
```

---

# 14. UTILITIES

# Required Utility Classes

- JwtUtil
- DateTimeUtil
- ValidationUtil
- PaginationUtil
- SecurityUtil

---

# 15. CONFIGURATIONS

# application.yml

Required Configurations:

- PostgreSQL datasource
- JWT secret
- Swagger
- Logging
- JPA
- Flyway
- Razorpay keys

---

# SecurityConfig

Responsibilities:

- Disable CSRF
- Stateless sessions
- JWT filter chain
- Endpoint authorization

---

# OpenAPI Config

Enable:

- Swagger UI
- API docs
- JWT Authorization header

---

# 16. DATABASE MIGRATION

Use:

```text
Flyway
```

Migration Structure:

```text
V1__create_roles.sql
V2__create_users.sql
V3__create_expert_profile.sql
V4__create_booking.sql
```

---

# 17. LOGGING

Use:

```text
SLF4J + Logback
```

Log:

- API requests
- Exceptions
- Payment callbacks
- Booking flow

Avoid logging:

- passwords
- JWT secrets
- sensitive data

---

# 18. SEARCH IMPLEMENTATION

# Phase 1

Use PostgreSQL full-text search.

Search Filters:

- expertise
- rating
- hourly rate
- experience
- availability

---

# 19. PAYMENT FLOW

Recommended Gateway:

- Razorpay

Flow:

```text
User -> Create Booking
     -> Initiate Payment
     -> Gateway Payment
     -> Verify Payment
     -> Confirm Booking
```

---

# 20. BUSINESS RULES

# Booking Rules

- Expert cannot book own slot
- Slot cannot be double booked
- Booking requires successful payment
- Cancelled slots become available again

---

# Review Rules

- Review only after completed session
- One review per booking

---

# Expert Rules

- Expert profile requires ROLE_EXPERT
- Verification required for public listing

---

# 21. SECURITY BEST PRACTICES

- BCrypt password hashing
- JWT expiration
- Refresh token rotation
- Input validation
- SQL injection prevention
- XSS prevention
- Rate limiting
- HTTPS only

---

# 22. FUTURE SCALABILITY

Future migration to microservices:

- Auth Service
- Booking Service
- Payment Service
- Search Service
- Notification Service

Current modular monolith structure supports smooth migration.

---

# 23. DEVELOPMENT ORDER

Recommended order:

```text
1. Project Setup
2. Security Configuration
3. Role Entity
4. User Module
5. Authentication Module
6. Expert Module
7. Availability Module
8. Booking Module
9. Payment Module
10. Review Module
11. Session Module
12. Search Optimization
13. Swagger
14. Exception Handling
15. Dockerization
```

---

# 24. DOCKER SETUP

Required Containers:

- Spring Boot App
- PostgreSQL

Future:

- Redis
- Kafka
- OpenSearch

---

# 25. FINAL MVP FEATURES

By end of Phase 1, platform supports:

- User Registration/Login
- Expert Onboarding
- Expert Search
- Slot Management
- Session Booking
- Online Payments
- Reviews & Ratings
- Session Tracking
- JWT Security
- Swagger APIs
- PostgreSQL Persistence
- Dockerized Deployment
