package com.platform.drockley.service;

import com.platform.drockley.common.dto.PaymentResponse;

import java.util.Map;
import java.util.UUID;

/**
 * Service interface for payment processing operations.
 */
public interface PaymentService {

    /**
     * Initiates a payment for a booking.
     *
     * @param bookingId the ID of the booking to initiate payment for
     * @return the payment response containing payment details
     */
    PaymentResponse initiatePayment(UUID bookingId);

    /**
     * Verifies a payment using Razorpay signature and payment details.
     *
     * @param razorpayOrderId the Razorpay order ID
     * @param razorpayPaymentId the Razorpay payment ID
     * @param razorpaySignature the Razorpay signature to verify authenticity
     * @return the payment response if verification is successful
     */
    PaymentResponse verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature);

    /**
     * Retrieves payment information for a booking.
     *
     * @param bookingId the ID of the booking
     * @return the payment response
     */
    PaymentResponse getPaymentByBooking(UUID bookingId);

    /**
     * Handles payment webhook callback from Razorpay.
     *
     * @param webhookData the webhook payload containing payment information
     */
    void handlePaymentCallback(Map<String, String> webhookData);
}
