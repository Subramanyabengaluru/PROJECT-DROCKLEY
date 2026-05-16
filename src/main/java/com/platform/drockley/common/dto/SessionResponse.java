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
public class SessionResponse {
    private UUID id;
    private UUID bookingId;
    private String meetingLink;
    private String sessionStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String recordingUrl;
}
