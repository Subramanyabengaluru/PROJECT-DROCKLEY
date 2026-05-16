package com.platform.drockley.service.impl;

import com.platform.drockley.common.dto.CreateReviewRequest;
import com.platform.drockley.common.dto.ReviewResponse;
import com.platform.drockley.common.exception.BadRequestException;
import com.platform.drockley.common.exception.NotFoundException;
import com.platform.drockley.entity.Booking;
import com.platform.drockley.entity.ExpertProfile;
import com.platform.drockley.entity.Review;
import com.platform.drockley.entity.User;
import com.platform.drockley.enums.BookingStatus;
import com.platform.drockley.repository.BookingRepository;
import com.platform.drockley.repository.ExpertRepository;
import com.platform.drockley.repository.ReviewRepository;
import com.platform.drockley.repository.UserRepository;
import com.platform.drockley.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ExpertRepository expertRepository;

    @Override
    public ReviewResponse addReview(UUID userId, CreateReviewRequest request) {
        log.debug("Adding review for userId: {}, bookingId: {}", userId, request.getBookingId());

        User reviewer = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + request.getBookingId()));

        if (!validateReviewEligibility(userId, request.getBookingId())) {
            throw new BadRequestException("User is not eligible to review this booking");
        }

        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new BadRequestException("Review already exists for this booking");
        }

        ExpertProfile expertProfile = booking.getExpertProfile();

        Review review = Review.builder()
                .booking(booking)
                .reviewer(reviewer)
                .expertProfile(expertProfile)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Review added successfully for userId: {}, bookingId: {}", userId, request.getBookingId());

        updateExpertRating(expertProfile.getId());

        return mapReviewToResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByExpert(UUID expertId) {
        log.debug("Fetching reviews for expertId: {}", expertId);

        ExpertProfile expertProfile = expertRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("Expert profile not found with id: " + expertId));

        List<Review> reviews = reviewRepository.findByExpertProfileId(expertId);
        return reviews.stream()
                .map(this::mapReviewToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateExpertRating(UUID expertId) {
        log.debug("Updating expert rating for expertId: {}", expertId);

        ExpertProfile expertProfile = expertRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("Expert profile not found with id: " + expertId));

        List<Review> reviews = reviewRepository.findByExpertProfileId(expertId);
        if (reviews.isEmpty()) {
            expertProfile.setAvgRating(0.0);
        } else {
            double averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            expertProfile.setAvgRating(averageRating);
        }

        expertRepository.save(expertProfile);
        log.info("Expert rating updated successfully for expertId: {}", expertId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateReviewEligibility(UUID userId, UUID bookingId) {
        log.debug("Validating review eligibility for userId: {}, bookingId: {}", userId, bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            return false;
        }

        if (booking.getBookingStatus() != BookingStatus.COMPLETED) {
            return false;
        }

        return !reviewRepository.existsByBookingId(bookingId);
    }

    private ReviewResponse mapReviewToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(review.getBooking().getId())
                .reviewerId(review.getReviewer().getId())
                .expertProfileId(review.getExpertProfile().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
