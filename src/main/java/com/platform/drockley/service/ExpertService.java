package com.platform.drockley.service;

import com.platform.drockley.common.dto.CreateExpertProfileRequest;
import com.platform.drockley.common.dto.ExpertProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for expert profile management operations.
 */
public interface ExpertService {

    /**
     * Creates a new expert profile for a user.
     *
     * @param userId the ID of the user creating the expert profile
     * @param request the expert profile creation request
     * @return the created expert profile response
     */
    ExpertProfileResponse createExpertProfile(UUID userId, CreateExpertProfileRequest request);

    /**
     * Updates an existing expert profile.
     *
     * @param expertId the ID of the expert profile to update
     * @param request the expert profile update request
     * @return the updated expert profile response
     */
    ExpertProfileResponse updateExpertProfile(UUID expertId, CreateExpertProfileRequest request);

    /**
     * Retrieves an expert profile by their ID.
     *
     * @param expertId the ID of the expert profile to retrieve
     * @return the expert profile response
     */
    ExpertProfileResponse getExpertProfile(UUID expertId);

    /**
     * Searches for verified experts by query string.
     *
     * @param query the search query
     * @param pageable pagination information
     * @return a page of matching expert profiles
     */
    Page<ExpertProfileResponse> searchExpertsByQuery(String query, Pageable pageable);

    /**
     * Retrieves the top-rated verified experts.
     *
     * @param pageable pagination information
     * @return a page of top-rated expert profiles
     */
    Page<ExpertProfileResponse> getTopRatedExperts(Pageable pageable);

    /**
     * Adds expertise areas to an expert profile.
     *
     * @param expertId the ID of the expert profile
     * @param expertiseIds the IDs of expertise areas to add
     */
    void addExpertise(UUID expertId, Set<UUID> expertiseIds);

    /**
     * Calculates and updates the average rating for an expert.
     *
     * @param expertId the ID of the expert profile
     * @return the calculated average rating
     */
    Double calculateAverageRating(UUID expertId);

    /**
     * Increments the total sessions count for an expert.
     *
     * @param expertId the ID of the expert profile
     */
    void incrementTotalSessions(UUID expertId);
}
