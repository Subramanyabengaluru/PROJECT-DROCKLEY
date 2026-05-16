package com.platform.drockley.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "availability_slots",
        indexes = {
                @Index(name = "idx_slot_expert", columnList = "expert_profile_id"),
                @Index(name = "idx_slot_start_time", columnList = "start_time"),
                @Index(name = "idx_slot_booked", columnList = "booked")
        }
)
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_profile_id", nullable = false)
    private ExpertProfile expertProfile;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";

    @Column(name = "booked", nullable = false)
    private Boolean booked = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "availabilitySlot", fetch = FetchType.LAZY)
    private Booking booking;
}
