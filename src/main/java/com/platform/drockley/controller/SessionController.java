package com.platform.drockley.controller;

import com.platform.drockley.common.dto.ApiResponse;
import com.platform.drockley.common.dto.CreateSessionRequest;
import com.platform.drockley.common.dto.MeetingLinkRequest;
import com.platform.drockley.common.dto.RecordingUrlRequest;
import com.platform.drockley.common.dto.SessionResponse;
import com.platform.drockley.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(@Valid @RequestBody CreateSessionRequest request) {
        SessionResponse response = sessionService.createSession(request.getBookingId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Session created successfully", response));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse<SessionResponse>> startSession(@PathVariable UUID id) {
        SessionResponse response = sessionService.startSession(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session started successfully", response));
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<ApiResponse<SessionResponse>> endSession(@PathVariable UUID id) {
        SessionResponse response = sessionService.endSession(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Session ended successfully", response));
    }

    @PutMapping("/{id}/meeting-link")
    public ResponseEntity<ApiResponse<SessionResponse>> saveMeetingLink(
            @PathVariable UUID id,
            @Valid @RequestBody MeetingLinkRequest request
    ) {
        SessionResponse response = sessionService.saveMeetingLink(id, request.getMeetingLink());
        return ResponseEntity.ok(new ApiResponse<>(true, "Meeting link saved successfully", response));
    }

    @PutMapping("/{id}/recording-url")
    public ResponseEntity<ApiResponse<SessionResponse>> saveRecordingUrl(
            @PathVariable UUID id,
            @Valid @RequestBody RecordingUrlRequest request
    ) {
        SessionResponse response = sessionService.saveRecordingUrl(id, request.getRecordingUrl());
        return ResponseEntity.ok(new ApiResponse<>(true, "Recording URL saved successfully", response));
    }
}
