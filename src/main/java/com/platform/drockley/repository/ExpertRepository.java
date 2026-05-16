package com.platform.drockley.repository;

import com.platform.drockley.entity.ExpertProfile;
import com.platform.drockley.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpertRepository extends JpaRepository<ExpertProfile, UUID> {
    Optional<ExpertProfile> findByUserId(UUID userId);
    
    Page<ExpertProfile> findByVerificationStatus(VerificationStatus status, Pageable pageable);
    
    @Query("SELECT e FROM ExpertProfile e WHERE e.verificationStatus = 'VERIFIED' " +
           "AND (LOWER(e.headline) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.bio) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ExpertProfile> searchExpertsByQuery(@Param("query") String query, Pageable pageable);
    
    Page<ExpertProfile> findByVerificationStatusOrderByAvgRatingDesc(VerificationStatus status, Pageable pageable);
}
