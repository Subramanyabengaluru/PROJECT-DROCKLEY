package com.platform.drockley.repository;

import com.platform.drockley.entity.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilitySlot, UUID> {
    
    List<AvailabilitySlot> findByExpertProfileIdAndBookedFalse(UUID expertId);
    
    @Query("SELECT a FROM AvailabilitySlot a WHERE a.expertProfile.id = :expertId " +
           "AND a.booked = false AND a.startTime >= :now ORDER BY a.startTime ASC")
    List<AvailabilitySlot> findAvailableSlotsByExpert(@Param("expertId") UUID expertId, 
                                                      @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM AvailabilitySlot a WHERE a.expertProfile.id = :expertId " +
           "AND ((a.startTime < :endTime AND a.endTime > :startTime) AND a.booked = false)")
    List<AvailabilitySlot> findOverlappingSlots(@Param("expertId") UUID expertId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);
}
