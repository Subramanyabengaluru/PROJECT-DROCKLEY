package com.platform.drockley.service.impl;

import com.platform.drockley.common.dto.BookingResponse;
import com.platform.drockley.common.dto.CreateBookingRequest;
import com.platform.drockley.common.exception.BadRequestException;
import com.platform.drockley.common.exception.BookingConflictException;
import com.platform.drockley.common.exception.NotFoundException;
import com.platform.drockley.entity.AvailabilitySlot;
import com.platform.drockley.entity.Booking;
import com.platform.drockley.entity.ExpertProfile;
import com.platform.drockley.entity.User;
import com.platform.drockley.enums.BookingStatus;
import com.platform.drockley.enums.PaymentStatus;
import com.platform.drockley.repository.AvailabilityRepository;
import com.platform.drockley.repository.BookingRepository;
import com.platform.drockley.repository.ExpertRepository;
import com.platform.drockley.repository.UserRepository;
import com.platform.drockley.service.BookingService;
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
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ExpertRepository expertRepository;
    private final AvailabilityRepository availabilityRepository;

    @Override
    public BookingResponse createBooking(UUID userId, CreateBookingRequest request) {
        log.debug("Creating booking for userId: {}, slotId: {}", userId, request.getAvailabilitySlotId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        AvailabilitySlot slot = availabilityRepository.findById(request.getAvailabilitySlotId())
                .orElseThrow(() -> new NotFoundException("Availability slot not found with id: " + request.getAvailabilitySlotId()));

        if (!validateBookingEligibility(userId, request.getAvailabilitySlotId())) {
            throw new BadRequestException("User is not eligible to book this slot");
        }

        if (slot.getBooked()) {
            throw new BookingConflictException("This availability slot is already booked");
        }

        ExpertProfile expertProfile = slot.getExpertProfile();

        if (expertProfile.getUser().getId().equals(userId)) {
            throw new BadRequestException("Expert cannot book their own slots");
        }

        Booking booking = Booking.builder()
                .user(user)
                .expertProfile(expertProfile)
                .availabilitySlot(slot)
                .bookingStatus(BookingStatus.PENDING)
                .paymentStatus(PaymentStatus.INITIATED)
                .amount(expertProfile.getHourlyRate())
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully for userId: {}, slotId: {}", userId, request.getAvailabilitySlotId());

        return mapBookingToResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID bookingId) {
        log.debug("Fetching booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        return mapBookingToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(UUID userId) {
        log.debug("Fetching bookings for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(this::mapBookingToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        log.debug("Cancelling booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.COMPLETED || 
            booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot cancel a booking with status: " + booking.getBookingStatus());
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.getAvailabilitySlot().setBooked(false);
        bookingRepository.save(booking);
        log.info("Booking cancelled successfully with id: {}", bookingId);
    }

    @Override
    public void confirmBooking(UUID bookingId) {
        log.debug("Confirming booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (booking.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Payment must be completed before confirming the booking");
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.getAvailabilitySlot().setBooked(true);
        bookingRepository.save(booking);
        log.info("Booking confirmed successfully with id: {}", bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateBookingEligibility(UUID userId, UUID slotId) {
        log.debug("Validating booking eligibility for userId: {}, slotId: {}", userId, slotId);

        AvailabilitySlot slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Availability slot not found with id: " + slotId));

        if (slot.getBooked()) {
            return false;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        if (slot.getExpertProfile().getUser().getId().equals(userId)) {
            return false;
        }

        return true;
    }

    private BookingResponse mapBookingToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .expertProfileId(booking.getExpertProfile().getId())
                .availabilitySlotId(booking.getAvailabilitySlot().getId())
                .bookingStatus(booking.getBookingStatus().name())
                .paymentStatus(booking.getPaymentStatus().name())
                .amount(booking.getAmount())
                .bookingTime(booking.getBookingTime())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
