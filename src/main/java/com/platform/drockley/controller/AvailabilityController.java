package com.platform.drockley.controller;

import com.platform.drockley.common.dto.ApiResponse;
import com.platform.drockley.common.dto.AvailabilitySlotResponse;
import com.platform.drockley.common.dto.CreateAvailabilitySlotRequest;
import com.platform.drockley.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/slots")
    public ResponseEntity<ApiResponse<AvailabilitySlotResponse>> createSlot(
            @RequestParam UUID expertId,
            @Valid @RequestBody CreateAvailabilitySlotRequest request
    ) {
        AvailabilitySlotResponse response = availabilityService.createSlot(expertId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Availability slot created successfully", response));
    }

    @PutMapping("/slots/{id}")
    public ResponseEntity<ApiResponse<AvailabilitySlotResponse>> updateSlot(
            @PathVariable UUID id,
            @Valid @RequestBody CreateAvailabilitySlotRequest request
    ) {
        AvailabilitySlotResponse response = availabilityService.updateSlot(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability slot updated successfully", response));
    }

    @DeleteMapping("/slots/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSlot(@PathVariable UUID id) {
        availabilityService.deleteSlot(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability slot deleted successfully", null));
    }

    @GetMapping("/expert/{expertId}")
    public ResponseEntity<ApiResponse<List<AvailabilitySlotResponse>>> getAvailableSlots(@PathVariable UUID expertId) {
        List<AvailabilitySlotResponse> response = availabilityService.getAvailableSlots(expertId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Available slots fetched successfully", response));
    }
}
