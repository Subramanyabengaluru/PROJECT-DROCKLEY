package com.platform.drockley.service;

import com.platform.drockley.common.dto.CreateReviewRequest;
import com.platform.drockley.common.dto.ReviewResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for review and rating management operations.
 */
public interface ReviewService {

    /**
     * Adds a review for a booking.
     *
     * @param userId the ID of the user creating the review
     * @param request the review creation request
     * @return the created review response
     */
    ReviewResponse addReview(UUID userId, CreateReviewRequest request);

    /**
     * Retrieves all reviews for an expert.
     *
     * @param expertId the ID of the expert
     * @return a list of review responses for the expert
     */
    List<ReviewResponse> getReviewsByExpert(UUID expertId);

    /**
     * Updates the average rating for an expert based on all their reviews.
     *
     * @param expertId the ID of the expert
     */
    void updateExpertRating(UUID expertId);

    /**
     * Validates if a user is eligible to review a specific booking.
     *
     * @param userId the ID of the user
     * @param bookingId the ID of the booking
     * @return true if the user is eligible to review the booking, false otherwise
     */
    boolean validateReviewEligibility(UUID userId, UUID bookingId);
}
