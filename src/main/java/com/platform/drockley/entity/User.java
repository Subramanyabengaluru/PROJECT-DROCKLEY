package com.platform.drockley.entity;

import com.platform.drockley.enums.Role;
import com.platform.drockley.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_status", columnList = "status")
        }
        )
public class User {

    /*      | Column              | Type                | Description        |
            | ------------------- | ------------------- | ------------------ |
            | id                  | UUID                | Primary key        |
            | first_name          | VARCHAR(100)        | User first name    |
            | last_name           | VARCHAR(100)        | User last name     |
            | email               | VARCHAR(255) UNIQUE | Login email        |
            | password            | VARCHAR(255)        | Encrypted password |
            | phone_number        | VARCHAR(20)         | Contact number     |
            | role_id             | FK                  | User role          |
            | status              | ENUM                | ACTIVE/BLOCKED/etc |
            | email_verified      | BOOLEAN             | Email verification |
            | profile_picture_url | TEXT                | Profile image      |
            | last_login_at       | TIMESTAMP           | Last login         |
            | created_at          | TIMESTAMP           | Created time       |
            | updated_at          | TIMESTAMP           | Updated time       |
    */

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;


    @Email
    @NotNull
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @NotNull
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
        @Column(name = "role", nullable = false)
        private Role role;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;


    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ExpertProfile expertProfile;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private java.util.List<Booking> bookings = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "reviewer", fetch = FetchType.LAZY)
    private java.util.List<Review> reviews = new java.util.ArrayList<>();

}
