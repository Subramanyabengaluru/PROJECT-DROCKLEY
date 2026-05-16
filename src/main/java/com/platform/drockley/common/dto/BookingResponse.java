package com.platform.drockley.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private UUID id;
    private UUID userId;
    private UUID expertProfileId;
    private UUID availabilitySlotId;
    private String bookingStatus;
    private String paymentStatus;
    private BigDecimal amount;
    private LocalDateTime bookingTime;
    private LocalDateTime createdAt;
}
