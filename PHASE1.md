Phase 1 — Monolithic Backend: Tasks & Implementation Plan

Summary
-------
This file lists exactly what needs to be completed for Phase 1 (MVP) of the Education Consultation Platform monolith. It includes the entities, the layers to implement for each entity (Controller / Service / Repository / DTO / Mapper), configuration pieces (security, caching), utilities, and an ordered implementation plan.

Requirements checklist (from user request)
-----------------------------------------
- [ ] List all entities required (explicit)
- [ ] Add Controller, Service, Repository layers for each entity
- [ ] Add DTOs and mappers to avoid exposing entities
- [ ] Add configuration (Security, Caching, Swagger, DB migrations)
- [ ] Add utility classes (exceptions, response wrappers, validation)
- [ ] Add JWT-based authentication and role-based authorization
- [ ] Add Redis caching configuration (if required)
- [ ] Add placeholders/interfaces for external integrations (payments, email, video)
- [ ] Provide phased MVP task order and priorities

Phase 1 (MVP) entities
----------------------
Core domain entities we will implement in Phase 1:
- User (exists) — authentication + basic profile
- Role (enum exists) — RBAC
- ExpertProfile — expert-specific data (bio, hourlyRate, headline, verificationStatus)
- Expertise (tag/skill) — topic/category taxonomy
- ExpertProfileSkill (join table) — many-to-many mapping between ExpertProfile and Expertise
- AvailabilitySlot — expert available slots (startTime, endTime, timezone, slotStatus)
- Booking — booking record linking User, ExpertProfile, AvailabilitySlot
- Payment — payment tracking for booking
- Review — rating + comment for completed booking
- Session — actual session metadata (meetingLink, recordingUrl, status)

Auxiliary entities / audit / enums
- Audit / AuditLog (optional) — actions for admin/traceability
- Notification (optional for queued notifications)
- Media (optional) — file metadata for profile pictures, videos

For each entity implement (pattern)
----------------------------------
- src/main/java/com/platform/drockley/<module>/entity/<Entity>.java
- src/main/java/com/platform/drockley/<module>/repository/<Entity>Repository.java (extends JpaRepository)
- src/main/java/com/platform/drockley/<module>/service/<Entity>Service.java (interface)
- src/main/java/com/platform/drockley/<module>/service/impl/<Entity>ServiceImpl.java
- src/main/java/com/platform/drockley/<module>/controller/<Entity>Controller.java (REST endpoints)
- src/main/java/com/platform/drockley/<module>/dto/* (Request/Response DTOs)
- src/main/java/com/platform/drockley/<module>/mapper/* (MapStruct or manual mappers)
- src/test/java/... unit tests for service and controller (happy path + 1 edge case)

Cross-cutting modules and classes
--------------------------------
- auth
  - AuthController (register, login, refresh)
  - AuthService (registration, authentication, token refresh)
  - JwtTokenProvider / JwtUtil
  - JwtAuthenticationFilter
  - SecurityConfig (PasswordEncoder bean, AuthenticationManager, endpoint security rules)
  - DTOs: LoginRequest, RegisterRequest, AuthResponse

- common / exception
  - ApiException, NotFoundException, BadRequestException
  - GlobalExceptionHandler (ControllerAdvice)
  - ApiResponse wrapper

- config
  - RedisConfig (Lettuce/RedisTemplate) — optional for caching sessions, rate limits
  - SwaggerConfig / OpenAPI
  - ModelMapperConfig / MapStruct configuration
  - DataSource/Flyway config (application.properties + migration scripts in db/migration)

- integration (interfaces / stubs)
  - PaymentGatewayService (startPayment, handleWebhook)
  - EmailService (sendVerification, sendBookingConfirmation)
  - VideoService (generateMeetingLink) — stub for Zoom/Meet or WebRTC

- util
  - DateTimeUtils (timezone helpers)
  - PasswordUtil if needed (wrap PasswordEncoder)
  - JwtUtils (token parsing)

- scheduling
  - BookingReminderScheduler (sends reminders via EmailService)

- search
  - SearchService (basic DB-based search for Phase 1). Later replace with OpenSearch integration.

Data modeling & DB
------------------
- Use UUID primary keys for entities (already used in User)
- Use enums for statuses (BookingStatus, PaymentStatus, UserStatus, VerificationStatus, SessionStatus)
- Use Flyway for DB migrations (create initial V1__init.sql)
- Add indexes on commonly searched fields (expert skills, ratings, availability times)

Security
--------
- JWT access tokens (short-lived) + refresh token support
- Spring Security to secure endpoints
- Passwords stored with BCrypt
- Role-based method and URL security (ROLE_USER, ROLE_EXPERT, ROLE_ADMIN)
- CSRF disabled for API-only (stateless)

Caching
-------
- Add Redis configuration and enable caching for heavy-read endpoints (expert search, profile lookup)
- Use Spring Cache abstraction with RedisCacheManager
- Cache invalidation rules: update or evict caches on profile/update/review/booking

APIs (minimum for Phase 1)
--------------------------
Auth
- POST /api/v1/auth/register
- POST /api/v1/auth/login
- POST /api/v1/auth/refresh

Users
- GET /api/v1/users/me
- PUT /api/v1/users/me

Experts
- POST /api/v1/experts (create profile)
- GET /api/v1/experts/{id}
- PUT /api/v1/experts/{id}
- GET /api/v1/experts (search/filter params)

Availability
- POST /api/v1/experts/{id}/availability
- GET /api/v1/experts/{id}/availability

Bookings
- POST /api/v1/bookings
- GET /api/v1/bookings/{id}
- PUT /api/v1/bookings/{id}/cancel

Payments (Phase1: stub + webhook)
- POST /api/v1/payments/initiate
- POST /api/v1/payments/webhook

Reviews
- POST /api/v1/reviews
- GET /api/v1/experts/{id}/reviews

Notes about integrations
------------------------
- Payment: implement PaymentGatewayService interface and a mock implementation for local testing; integrate real gateway (Stripe/Razorpay) later.
- Video: initially provide meetingLink generated by VideoService mock (UUID + instructions); support Zoom/Google Meet later.
- Email: use JavaMailSender for a SMTP provider or a mock implementation in dev.

Prioritized implementation order (Phase 1)
-----------------------------------------
1) Project setup/gradle deps & configuration (Spring Boot, Spring Data JPA, Lombok, Validation, Spring Security, JWT, Flyway, MapStruct or ModelMapper, Redis client, Swagger)
2) Auth module (register/login/refresh + Jwt tokens + UserRepository) — critical to secure other APIs
3) Role and basic User CRUD/profile endpoints
4) ExpertProfile + Expertise entities and endpoints
5) AvailabilitySlot entity + basic calendar slot creation
6) Booking flow (reserve slot, create booking, mark slot booked) — include idempotency checks
7) Payment stub & Payment entity; webhook endpoint to simulate success/failure
8) Review endpoints
9) Session entity + meeting link integration stub
10) Basic DB-based search endpoints for experts
11) Redis caching for read-heavy endpoints (expert search, profiles)
12) Add global exception handling, DTOs, mappers, and unit tests

What I'll do next (action)
--------------------------
- Create this Phase 1 plan file in the repo (done).
- Next implementation step (if you confirm): scaffold the Auth module (entities/dto/controller/service/repository) and security configuration.

Assumptions
-----------
- We keep a monolith for Phase 1 as requested.
- PostgreSQL will be used (application.properties already present).
- We'll use Spring Boot 3.x and Java 21 as suggested in the design doc.

Quality gates (how I'll validate)
---------------------------------
- After scaffolding Auth: run mvn/gradle build and run unit tests for AuthService
- Validate endpoints with simple integration tests (MockMvc)

Files/Classes to be created in the next implementation sprint (Auth skeleton)
-----------------------------------------------------------------------------
- src/main/java/com/platform/drockley/auth/entity/RefreshToken.java (optional)
- src/main/java/com/platform/drockley/auth/controller/AuthController.java
- src/main/java/com/platform/drockley/auth/service/AuthService.java
- src/main/java/com/platform/drockley/auth/service/impl/AuthServiceImpl.java
- src/main/java/com/platform/drockley/auth/dto/LoginRequest.java
- src/main/java/com/platform/drockley/auth/dto/RegisterRequest.java
- src/main/java/com/platform/drockley/auth/dto/AuthResponse.java
- src/main/java/com/platform/drockley/config/SecurityConfig.java
- src/main/java/com/platform/drockley/security/JwtTokenProvider.java
- src/main/java/com/platform/drockley/security/JwtAuthenticationFilter.java

Open questions (I'll assume defaults if you don't answer)
--------------------------------------------------------
- Do you want social login in Phase 1? (I'll skip and add placeholder interfaces)
- Payment gateway choice? (I'll scaffold a generic PaymentGatewayService; integrate Stripe/Razorpay later)

Done status
-----------
- [x] Read existing project and fixed a minor issue in `User.java` (role field mapping)
- [x] Added this Phase 1 plan file (`PHASE1.md`)



