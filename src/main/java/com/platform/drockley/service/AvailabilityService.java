package com.platform.drockley.service;

import com.platform.drockley.common.dto.AvailabilitySlotResponse;
import com.platform.drockley.common.dto.CreateAvailabilitySlotRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing expert availability slots.
 */
public interface AvailabilityService {

    /**
     * Creates a new availability slot for an expert.
     *
     * @param expertId the ID of the expert
     * @param request the availability slot creation request
     * @return the created availability slot response
     */
    AvailabilitySlotResponse createSlot(UUID expertId, CreateAvailabilitySlotRequest request);

    /**
     * Updates an existing availability slot.
     *
     * @param slotId the ID of the slot to update
     * @param request the slot update request
     * @return the updated availability slot response
     */
    AvailabilitySlotResponse updateSlot(UUID slotId, CreateAvailabilitySlotRequest request);

    /**
     * Deletes an availability slot.
     *
     * @param slotId the ID of the slot to delete
     */
    void deleteSlot(UUID slotId);

    /**
     * Retrieves all available (unbooked) slots for an expert.
     *
     * @param expertId the ID of the expert
     * @return a list of available slot responses
     */
    List<AvailabilitySlotResponse> getAvailableSlots(UUID expertId);

    /**
     * Validates if a time slot overlaps with existing slots for an expert.
     *
     * @param expertId the ID of the expert
     * @param start the start time of the slot to validate
     * @param end the end time of the slot to validate
     * @return true if there is an overlap, false otherwise
     */
    boolean validateSlotOverlap(UUID expertId, LocalDateTime start, LocalDateTime end);

    /**
     * Marks a slot as booked.
     *
     * @param slotId the ID of the slot to mark as booked
     */
    void markSlotAsBooked(UUID slotId);

    /**
     * Marks a slot as available (unbooked).
     *
     * @param slotId the ID of the slot to mark as available
     */
    void markSlotAsAvailable(UUID slotId);
}
