package com.platform.drockley.entity;

import com.platform.drockley.enums.VerificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "expert_profiles",
        indexes = {
                @Index(name = "idx_expert_user", columnList = "user_id"),
                @Index(name = "idx_expert_verification", columnList = "verification_status"),
                @Index(name = "idx_expert_rating", columnList = "avg_rating")
        }
)
public class ExpertProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank
    @Column(name = "headline", nullable = false, length = 255)
    private String headline;

    @NotBlank
    @Column(name = "bio", nullable = false, columnDefinition = "TEXT")
    private String bio;

    @Min(0)
    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;

    @Positive
    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "avg_rating", nullable = false)
    private Double avgRating = 0.0;

    @Column(name = "total_sessions", nullable = false)
    private Integer totalSessions = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "expert_expertise",
            joinColumns = @JoinColumn(name = "expert_id"),
            inverseJoinColumns = @JoinColumn(name = "expertise_id")
    )
    private Set<Expertise> expertises = new HashSet<>();

    @OneToMany(mappedBy = "expertProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AvailabilitySlot> availabilitySlots = new ArrayList<>();

    @OneToMany(mappedBy = "expertProfile", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "expertProfile", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
}
