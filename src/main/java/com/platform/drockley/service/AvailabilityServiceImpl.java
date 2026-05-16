package com.platform.drockley.service.impl;

import com.platform.drockley.common.dto.AvailabilitySlotResponse;
import com.platform.drockley.common.dto.CreateAvailabilitySlotRequest;
import com.platform.drockley.common.exception.BadRequestException;
import com.platform.drockley.common.exception.NotFoundException;
import com.platform.drockley.entity.AvailabilitySlot;
import com.platform.drockley.entity.ExpertProfile;
import com.platform.drockley.repository.AvailabilityRepository;
import com.platform.drockley.repository.ExpertRepository;
import com.platform.drockley.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final ExpertRepository expertRepository;

    @Override
    public AvailabilitySlotResponse createSlot(UUID expertId, CreateAvailabilitySlotRequest request) {
        log.debug("Creating availability slot for expertId: {}", expertId);

        ExpertProfile expertProfile = expertRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("Expert profile not found with id: " + expertId));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BadRequestException("Start time cannot be after end time");
        }

        if (validateSlotOverlap(expertId, request.getStartTime(), request.getEndTime())) {
            throw new BadRequestException("Time slot overlaps with existing availability slots");
        }

        AvailabilitySlot slot = AvailabilitySlot.builder()
                .expertProfile(expertProfile)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .timezone(request.getTimezone() != null ? request.getTimezone() : "UTC")
                .booked(false)
                .build();

        AvailabilitySlot savedSlot = availabilityRepository.save(slot);
        log.info("Availability slot created successfully for expertId: {}", expertId);

        return mapSlotToResponse(savedSlot);
    }

    @Override
    public AvailabilitySlotResponse updateSlot(UUID slotId, CreateAvailabilitySlotRequest request) {
        log.debug("Updating availability slot with id: {}", slotId);

        AvailabilitySlot slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Availability slot not found with id: " + slotId));

        if (slot.getBooked()) {
            throw new BadRequestException("Cannot update a booked availability slot");
        }

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BadRequestException("Start time cannot be after end time");
        }

        if (validateSlotOverlap(slot.getExpertProfile().getId(), request.getStartTime(), request.getEndTime())) {
            throw new BadRequestException("Time slot overlaps with existing availability slots");
        }

        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setTimezone(request.getTimezone() != null ? request.getTimezone() : "UTC");

        AvailabilitySlot updatedSlot = availabilityRepository.save(slot);
        log.info("Availability slot updated successfully with id: {}", slotId);

        return mapSlotToResponse(updatedSlot);
    }

    @Override
    public void deleteSlot(UUID slotId) {
        log.debug("Deleting availability slot with id: {}", slotId);

        AvailabilitySlot slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Availability slot not found with id: " + slotId));

        if (slot.getBooked()) {
            throw new BadRequestException("Cannot delete a booked availability slot");
        }

        availabilityRepository.deleteById(slotId);
        log.info("Availability slot deleted successfully with id: {}", slotId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilitySlotResponse> getAvailableSlots(UUID expertId) {
        log.debug("Fetching available slots for expertId: {}", expertId);

        List<AvailabilitySlot> slots = availabilityRepository.findAvailableSlotsByExpert(expertId, LocalDateTime.now());
        return slots.stream()
                .map(this::mapSlotToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateSlotOverlap(UUID expertId, LocalDateTime start, LocalDateTime end) {
        log.debug("Validating slot overlap for expertId: {}, start: {}, end: {}", expertId, start, end);

        List<AvailabilitySlot> overlappingSlots = availabilityRepository.findOverlappingSlots(expertId, start, end);
        return !overlappingSlots.isEmpty();
    }

    @Override
    public void markSlotAsBooked(UUID slotId) {
        log.debug("Marking slot as booked: {}", slotId);

        AvailabilitySlot slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Availability slot not found with id: " + slotId));

        slot.setBooked(true);
        availabilityRepository.save(slot);
        log.info("Slot marked as booked: {}", slotId);
    }

    @Override
    public void markSlotAsAvailable(UUID slotId) {
        log.debug("Marking slot as available: {}", slotId);

        AvailabilitySlot slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("Availability slot not found with id: " + slotId));

        slot.setBooked(false);
        availabilityRepository.save(slot);
        log.info("Slot marked as available: {}", slotId);
    }

    private AvailabilitySlotResponse mapSlotToResponse(AvailabilitySlot slot) {
        return AvailabilitySlotResponse.builder()
                .id(slot.getId())
                .expertProfileId(slot.getExpertProfile().getId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .timezone(slot.getTimezone())
                .booked(slot.getBooked())
                .createdAt(slot.getCreatedAt())
                .build();
    }
}
