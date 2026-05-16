package com.platform.drockley.service.impl;

import com.platform.drockley.common.dto.PaymentResponse;
import com.platform.drockley.common.exception.BadRequestException;
import com.platform.drockley.common.exception.NotFoundException;
import com.platform.drockley.entity.Booking;
import com.platform.drockley.entity.Payment;
import com.platform.drockley.enums.PaymentStatus;
import com.platform.drockley.repository.BookingRepository;
import com.platform.drockley.repository.PaymentRepository;
import com.platform.drockley.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    @Override
    public PaymentResponse initiatePayment(UUID bookingId) {
        log.debug("Initiating payment for bookingId: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getAmount())
                .paymentStatus(PaymentStatus.INITIATED)
                .currency("INR")
                .paymentGateway("RAZORPAY")
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment initiated successfully for bookingId: {}, paymentId: {}", bookingId, savedPayment.getId());

        return mapPaymentToResponse(savedPayment);
    }

    @Override
    public PaymentResponse verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        log.debug("Verifying payment with orderId: {}, paymentId: {}", razorpayOrderId, razorpayPaymentId);

        Payment payment = paymentRepository.findByTransactionId(razorpayPaymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found with transactionId: " + razorpayPaymentId));

        if (!verifyRazorpaySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature)) {
            throw new BadRequestException("Invalid Razorpay signature");
        }

        payment.setTransactionId(razorpayPaymentId);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        Booking booking = payment.getBooking();
        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        bookingRepository.save(booking);

        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment verified successfully for paymentId: {}", razorpayPaymentId);

        return mapPaymentToResponse(updatedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBooking(UUID bookingId) {
        log.debug("Fetching payment for bookingId: {}", bookingId);

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new NotFoundException("Payment not found for bookingId: " + bookingId));

        return mapPaymentToResponse(payment);
    }

    @Override
    public void handlePaymentCallback(Map<String, String> webhookData) {
        log.debug("Handling payment webhook callback");

        String transactionId = webhookData.get("payment_id");
        String orderId = webhookData.get("order_id");
        String signature = webhookData.get("signature");

        if (transactionId == null || orderId == null || signature == null) {
            throw new BadRequestException("Missing required webhook parameters");
        }

        verifyPayment(orderId, transactionId, signature);
        log.info("Payment webhook handled successfully for transactionId: {}", transactionId);
    }

    private boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) {
        log.debug("Verifying Razorpay signature for orderId: {}", orderId);

        try {
            String payload = orderId + "|" + paymentId;
            String expectedSignature = generateSignature(payload, razorpayKeySecret);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying Razorpay signature", e);
            return false;
        }
    }

    private String generateSignature(String payload, String secret) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(
                    secret.getBytes("UTF-8"), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes("UTF-8"));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.error("Error generating signature", e);
            return "";
        }
    }

    private PaymentResponse mapPaymentToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBooking().getId())
                .paymentGateway(payment.getPaymentGateway())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentStatus(payment.getPaymentStatus().name())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
