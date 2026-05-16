package com.platform.drockley.service.impl;

import com.platform.drockley.common.dto.CreateExpertProfileRequest;
import com.platform.drockley.common.dto.ExpertProfileResponse;
import com.platform.drockley.common.dto.ExpertiseDto;
import com.platform.drockley.common.exception.NotFoundException;
import com.platform.drockley.entity.ExpertProfile;
import com.platform.drockley.entity.Expertise;
import com.platform.drockley.entity.User;
import com.platform.drockley.enums.VerificationStatus;
import com.platform.drockley.repository.ExpertRepository;
import com.platform.drockley.repository.ExpertiseRepository;
import com.platform.drockley.repository.ReviewRepository;
import com.platform.drockley.repository.UserRepository;
import com.platform.drockley.service.ExpertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExpertServiceImpl implements ExpertService {

    private final ExpertRepository expertRepository;
    private final UserRepository userRepository;
    private final ExpertiseRepository expertiseRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public ExpertProfileResponse createExpertProfile(UUID userId, CreateExpertProfileRequest request) {
        log.debug("Creating expert profile for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        ExpertProfile expertProfile = ExpertProfile.builder()
                .user(user)
                .headline(request.getHeadline())
                .bio(request.getBio())
                .experienceYears(request.getExperienceYears())
                .hourlyRate(request.getHourlyRate())
                .linkedinUrl(request.getLinkedinUrl())
                .verificationStatus(VerificationStatus.PENDING)
                .avgRating(0.0)
                .totalSessions(0)
                .build();

        Set<Expertise> expertises = new HashSet<>();
        if (request.getExpertiseIds() != null && !request.getExpertiseIds().isEmpty()) {
            expertises = request.getExpertiseIds().stream()
                    .map(expertiseId -> expertiseRepository.findById(expertiseId)
                            .orElseThrow(() -> new NotFoundException("Expertise not found with id: " + expertiseId)))
                    .collect(Collectors.toSet());
        }
        expertProfile.setExpertises(expertises);

        ExpertProfile savedExpert = expertRepository.save(expertProfile);
        log.info("Expert profile created successfully for userId: {}", userId);

        return mapExpertToResponse(savedExpert);
    }

    @Override
    public ExpertProfileResponse updateExpertProfile(UUID expertId, CreateExpertProfileRequest request) {
        log.debug("Updating expert profile for expertId: {}", expertId);

        ExpertProfile expertProfile = expertRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("Expert profile not found with id: " + expertId));

        expertProfile.setHeadline(request.getHeadline());
        expertProfile.setBio(request.getBio());
        expertProfile.setExperienceYears(request.getExperienceYears());
        expertProfile.setHourlyRate(request.getHourlyRate());
        expertProfile.setLinkedinUrl(request.getLinkedinUrl());

        if (request.getExpertiseIds() != null && !request.getExpertiseIds().isEmpty()) {
            Set<Expertise> expertises = request.getExpertiseIds().stream()
                    .map(expertiseId -> expertiseRepository.findById(expertiseId)
                            .orElseThrow(() -> new NotFoundException("Expertise not found with id: " + expertiseId)))
                    .collect(Collectors.toSet());
            expertProfile.setExpertises(expertises);
        }

        ExpertProfile updatedExpert = expertRepository.save(expertProfile);
        log.info("Expert profile updated successfully for expertId: {}", expertId);

        return mapExpertToResponse(updatedExpert);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpertProfileResponse getExpertProfile(UUID expertId) {
        log.debug("Fetching expert profile with id: {}", expertId);

        ExpertProfile expertProfile = expertRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("Expert profile not found with id: " + expertId));

        return mapExpertToResponse(expertProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpertProfileResponse> searchExpertsByQuery(String query, Pageable pageable) {
        log.debug("Searching experts with query: {}", query);

        Page<ExpertProfile> experts = expertRepository.searchExpertsByQuery(query, pageable);
        return experts.map(this::mapExpertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpertProfileResponse> getTopRatedExperts(Pageable pageable) {
        log.debug("Fetching top-rated experts");

        Page<ExpertProfile> experts = expertRepository.findByVerificationStatusOrderByAvgRatingDesc(VerificationStatus.VERIFIED, pageable);
        return experts.map(this::mapExpertToResponse);
    }

    @Override
    public void addExpertise(UUID expertId, Set<UUID> expertiseIds) {
        log.debug("Adding expertise to expert: {}", expertId);

        ExpertProfile expertProfile = expertRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("Expert profile not found with id: " + expertId));

        Set<Expertise> expertises = expertiseIds.stream()
                .map(expertiseId -> expertiseRepository.findById(expertiseId)
                        .orElseThrow(() -> new NotFoundException("Expertise not found with id: " + expertiseId)))
                .collect(Collectors.toSet());

        expertProfile.getExpertises().addAll(expertises);
        expertRepository.save(expertProfile);
        log.info("Expertise added successfully to expert: {}", expertId);
    }

    @Override
    public Double calculateAverageRating(UUID expertId) {
        log.debug("Calculating average rating for expert: {}", expertId);

        ExpertProfile expertProfile = expertRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("Expert profile not found with id: " + expertId));

        long reviewCount = reviewRepository.countByExpertProfileId(expertId);
        if (reviewCount == 0) {
            log.info("No reviews found for expert: {}", expertId);
            return 0.0;
        }

        double averageRating = reviewRepository.findByExpertProfileId(expertId).stream()
                .mapToInt(review -> review.getRating())
                .average()
                .orElse(0.0);

        expertProfile.setAvgRating(averageRating);
        expertRepository.save(expertProfile);
        log.info("Average rating calculated and updated for expert: {}, rating: {}", expertId, averageRating);

        return averageRating;
    }

    @Override
    public void incrementTotalSessions(UUID expertId) {
        log.debug("Incrementing total sessions for expert: {}", expertId);

        ExpertProfile expertProfile = expertRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("Expert profile not found with id: " + expertId));

        expertProfile.setTotalSessions(expertProfile.getTotalSessions() + 1);
        expertRepository.save(expertProfile);
        log.info("Total sessions incremented for expert: {}", expertId);
    }

    private ExpertProfileResponse mapExpertToResponse(ExpertProfile expertProfile) {
        Set<ExpertiseDto> expertiseDtos = expertProfile.getExpertises().stream()
                .map(expertise -> ExpertiseDto.builder()
                        .id(expertise.getId())
                        .name(expertise.getName())
                        .category(expertise.getCategory())
                        .build())
                .collect(Collectors.toSet());

        return ExpertProfileResponse.builder()
                .id(expertProfile.getId())
                .userId(expertProfile.getUser().getId())
                .headline(expertProfile.getHeadline())
                .bio(expertProfile.getBio())
                .experienceYears(expertProfile.getExperienceYears())
                .hourlyRate(expertProfile.getHourlyRate())
                .avgRating(expertProfile.getAvgRating())
                .totalSessions(expertProfile.getTotalSessions())
                .verificationStatus(expertProfile.getVerificationStatus().name())
                .linkedinUrl(expertProfile.getLinkedinUrl())
                .expertises(expertiseDtos)
                .createdAt(expertProfile.getCreatedAt())
                .updatedAt(expertProfile.getUpdatedAt())
                .build();
    }
}
