package com.platform.drockley.controller;

import com.platform.drockley.common.dto.ApiResponse;
import com.platform.drockley.common.dto.AvailabilitySlotResponse;
import com.platform.drockley.common.dto.CreateExpertProfileRequest;
import com.platform.drockley.common.dto.ExpertProfileResponse;
import com.platform.drockley.common.dto.UserResponse;
import com.platform.drockley.common.exception.UnauthorizedException;
import com.platform.drockley.service.AvailabilityService;
import com.platform.drockley.service.ExpertService;
import com.platform.drockley.service.UserService;
import com.platform.drockley.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experts")
@RequiredArgsConstructor
public class ExpertController {

    private final ExpertService expertService;
    private final AvailabilityService availabilityService;
    private final UserService userService;

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<ExpertProfileResponse>> createProfile(@Valid @RequestBody CreateExpertProfileRequest request) {
        ExpertProfileResponse response = expertService.createExpertProfile(currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Expert profile created successfully", response));
    }

    @PutMapping("/profile/{expertId}")
    public ResponseEntity<ApiResponse<ExpertProfileResponse>> updateProfile(
            @PathVariable UUID expertId,
            @Valid @RequestBody CreateExpertProfileRequest request
    ) {
        ExpertProfileResponse response = expertService.updateExpertProfile(expertId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Expert profile updated successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpertProfileResponse>> getProfile(@PathVariable UUID id) {
        ExpertProfileResponse response = expertService.getExpertProfile(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Expert profile fetched successfully", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ExpertProfileResponse>>> searchExperts(
            @RequestParam(defaultValue = "") String query,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ExpertProfileResponse> response = expertService.searchExpertsByQuery(query, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Experts fetched successfully", response));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<Page<ExpertProfileResponse>>> getTopRatedExperts(@PageableDefault(size = 10) Pageable pageable) {
        Page<ExpertProfileResponse> response = expertService.getTopRatedExperts(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Top rated experts fetched successfully", response));
    }

    @GetMapping("/availability/{expertId}")
    public ResponseEntity<ApiResponse<List<AvailabilitySlotResponse>>> getExpertAvailability(@PathVariable UUID expertId) {
        List<AvailabilitySlotResponse> response = availabilityService.getAvailableSlots(expertId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Expert availability fetched successfully", response));
    }

    @PostMapping("/{expertId}/expertise")
    public ResponseEntity<ApiResponse<Void>> addExpertise(@PathVariable UUID expertId, @RequestBody Set<UUID> expertiseIds) {
        expertService.addExpertise(expertId, expertiseIds);
        return ResponseEntity.ok(new ApiResponse<>(true, "Expertise added successfully", null));
    }

    private UUID currentUserId() {
        String email = SecurityUtil.getCurrentUserEmail();
        if (email == null || email.isBlank()) {
            throw new UnauthorizedException("Authenticated user is required");
        }
        UserResponse user = userService.getUserByEmail(email);
        return user.getId();
    }
}
