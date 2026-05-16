package com.platform.drockley.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpertProfileResponse {
    private UUID id;
    private UUID userId;
    private String headline;
    private String bio;
    private Integer experienceYears;
    private BigDecimal hourlyRate;
    private Double avgRating;
    private Integer totalSessions;
    private String verificationStatus;
    private String linkedinUrl;
    private Set<ExpertiseDto> expertises;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
