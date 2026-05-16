package com.platform.drockley.repository;

import com.platform.drockley.entity.Payment;
import com.platform.drockley.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    Optional<Payment> findByBookingId(UUID bookingId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    long countByPaymentStatus(PaymentStatus status);
}
