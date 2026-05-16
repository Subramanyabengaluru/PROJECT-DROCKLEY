package com.platform.drockley.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilitySlotResponse {
    private UUID id;
    private UUID expertProfileId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String timezone;
    private Boolean booked;
    private LocalDateTime createdAt;
}
