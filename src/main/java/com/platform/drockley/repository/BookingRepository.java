package com.platform.drockley.repository;

import com.platform.drockley.entity.Booking;
import com.platform.drockley.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
    List<Booking> findByUserId(UUID userId);
    
    List<Booking> findByExpertProfileId(UUID expertId);
    
    List<Booking> findByUserIdAndBookingStatus(UUID userId, BookingStatus status);
    
    List<Booking> findByExpertProfileIdAndBookingStatus(UUID expertId, BookingStatus status);
    
    boolean existsByAvailabilitySlotIdAndBookingStatusNot(UUID slotId, BookingStatus status);
}
