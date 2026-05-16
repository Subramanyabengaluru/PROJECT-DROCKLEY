package com.platform.drockley.service;

import com.platform.drockley.common.dto.BookingResponse;
import com.platform.drockley.common.dto.CreateBookingRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for booking management operations.
 */
public interface BookingService {

    /**
     * Creates a new booking for a user.
     *
     * @param userId the ID of the user creating the booking
     * @param request the booking creation request
     * @return the created booking response
     */
    BookingResponse createBooking(UUID userId, CreateBookingRequest request);

    /**
     * Retrieves a booking by its ID.
     *
     * @param bookingId the ID of the booking to retrieve
     * @return the booking response
     */
    BookingResponse getBooking(UUID bookingId);

    /**
     * Retrieves all bookings for a user.
     *
     * @param userId the ID of the user
     * @return a list of booking responses for the user
     */
    List<BookingResponse> getMyBookings(UUID userId);

    /**
     * Cancels a booking.
     *
     * @param bookingId the ID of the booking to cancel
     */
    void cancelBooking(UUID bookingId);

    /**
     * Confirms a booking.
     *
     * @param bookingId the ID of the booking to confirm
     */
    void confirmBooking(UUID bookingId);

    /**
     * Validates if a user is eligible to book a specific availability slot.
     *
     * @param userId the ID of the user
     * @param slotId the ID of the availability slot
     * @return true if the user is eligible to book the slot, false otherwise
     */
    boolean validateBookingEligibility(UUID userId, UUID slotId);
}
