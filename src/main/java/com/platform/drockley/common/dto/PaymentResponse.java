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
public class PaymentResponse {
    private UUID id;
    private UUID bookingId;
    private String paymentGateway;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String paymentStatus;
    private LocalDateTime paidAt;
}
