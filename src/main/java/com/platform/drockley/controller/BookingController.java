package com.platform.drockley.controller;

import com.platform.drockley.common.dto.ApiResponse;
import com.platform.drockley.common.dto.BookingResponse;
import com.platform.drockley.common.dto.CreateBookingRequest;
import com.platform.drockley.common.dto.UserResponse;
import com.platform.drockley.common.exception.UnauthorizedException;
import com.platform.drockley.service.BookingService;
import com.platform.drockley.service.UserService;
import com.platform.drockley.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse response = bookingService.createBooking(currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Booking created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(@PathVariable UUID id) {
        BookingResponse response = bookingService.getBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking fetched successfully", response));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {
        List<BookingResponse> response = bookingService.getMyBookings(currentUserId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Bookings fetched successfully", response));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(@PathVariable UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking cancelled successfully", null));
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<ApiResponse<Void>> confirmBooking(@PathVariable UUID id) {
        bookingService.confirmBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking confirmed successfully", null));
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
