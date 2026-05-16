package com.platform.drockley.controller;

import com.platform.drockley.common.dto.ApiResponse;
import com.platform.drockley.common.dto.CreateReviewRequest;
import com.platform.drockley.common.dto.ReviewResponse;
import com.platform.drockley.common.dto.UserResponse;
import com.platform.drockley.common.exception.UnauthorizedException;
import com.platform.drockley.service.ReviewService;
import com.platform.drockley.service.UserService;
import com.platform.drockley.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(@Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.addReview(currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Review added successfully", response));
    }

    @GetMapping("/expert/{expertId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsByExpert(@PathVariable UUID expertId) {
        List<ReviewResponse> response = reviewService.getReviewsByExpert(expertId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reviews fetched successfully", response));
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
