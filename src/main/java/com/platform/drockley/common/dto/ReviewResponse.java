package com.platform.drockley.common.dto;

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
public class ReviewResponse {
    private UUID id;
    private UUID bookingId;
    private UUID reviewerId;
    private UUID expertProfileId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
