package com.platform.drockley.service;

import com.platform.drockley.common.dto.UpdateUserRequest;
import com.platform.drockley.common.dto.UserResponse;

import java.util.UUID;

/**
 * Service interface for user management operations.
 */
public interface UserService {

    /**
     * Updates a user's profile information.
     *
     * @param userId the ID of the user to update
     * @param request the update request containing new user information
     * @return the updated user response
     */
    UserResponse updateUserProfile(UUID userId, UpdateUserRequest request);

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return the user response
     */
    UserResponse getUserById(UUID userId);

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return the user response
     */
    UserResponse getUserByEmail(String email);

    /**
     * Soft deletes a user by marking their status as DELETED.
     *
     * @param userId the ID of the user to soft delete
     */
    void softDeleteUser(UUID userId);
}
