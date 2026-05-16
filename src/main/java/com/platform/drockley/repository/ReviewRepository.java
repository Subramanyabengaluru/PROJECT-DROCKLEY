package com.platform.drockley.repository;

import com.platform.drockley.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    List<Review> findByExpertProfileId(UUID expertId);
    
    Optional<Review> findByBookingId(UUID bookingId);
    
    boolean existsByBookingId(UUID bookingId);
    
    long countByExpertProfileId(UUID expertId);
}
