package com.platform.drockley.common.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExpertProfileRequest {
    
    @NotBlank
    @Size(min = 5, max = 255)
    private String headline;
    
    @NotBlank
    @Size(min = 20, max = 5000)
    private String bio;
    
    @NotNull
    @Min(0)
    private Integer experienceYears;
    
    @NotNull
    @Positive
    private BigDecimal hourlyRate;
    
    private String linkedinUrl;
    
    @NotNull
    private Set<UUID> expertiseIds;
}
