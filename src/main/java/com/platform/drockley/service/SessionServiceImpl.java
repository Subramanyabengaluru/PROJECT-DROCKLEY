package com.platform.drockley.service.impl;

import com.platform.drockley.common.dto.SessionResponse;
import com.platform.drockley.common.exception.BadRequestException;
import com.platform.drockley.common.exception.NotFoundException;
import com.platform.drockley.entity.Booking;
import com.platform.drockley.entity.Session;
import com.platform.drockley.enums.SessionStatus;
import com.platform.drockley.repository.BookingRepository;
import com.platform.drockley.repository.SessionRepository;
import com.platform.drockley.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final BookingRepository bookingRepository;

    @Override
    public SessionResponse createSession(UUID bookingId) {
        log.debug("Creating session for bookingId: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (sessionRepository.findByBookingId(bookingId).isPresent()) {
            throw new BadRequestException("Session already exists for this booking");
        }

        Session session = Session.builder()
                .booking(booking)
                .sessionStatus(SessionStatus.SCHEDULED)
                .build();

        Session savedSession = sessionRepository.save(session);
        log.info("Session created successfully for bookingId: {}", bookingId);

        return mapSessionToResponse(savedSession);
    }

    @Override
    public SessionResponse startSession(UUID sessionId) {
        log.debug("Starting session with id: {}", sessionId);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found with id: " + sessionId));

        if (session.getSessionStatus() != SessionStatus.SCHEDULED) {
            throw new BadRequestException("Cannot start a session with status: " + session.getSessionStatus());
        }

        session.setSessionStatus(SessionStatus.ONGOING);
        session.setStartTime(LocalDateTime.now());

        Session updatedSession = sessionRepository.save(session);
        log.info("Session started successfully with id: {}", sessionId);

        return mapSessionToResponse(updatedSession);
    }

    @Override
    public SessionResponse endSession(UUID sessionId) {
        log.debug("Ending session with id: {}", sessionId);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found with id: " + sessionId));

        if (session.getSessionStatus() != SessionStatus.ONGOING) {
            throw new BadRequestException("Cannot end a session with status: " + session.getSessionStatus());
        }

        session.setSessionStatus(SessionStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());

        Session updatedSession = sessionRepository.save(session);
        log.info("Session ended successfully with id: {}", sessionId);

        return mapSessionToResponse(updatedSession);
    }

    @Override
    public SessionResponse saveMeetingLink(UUID sessionId, String meetingLink) {
        log.debug("Saving meeting link for session: {}", sessionId);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found with id: " + sessionId));

        if (meetingLink == null || meetingLink.isBlank()) {
            throw new BadRequestException("Meeting link cannot be empty");
        }

        session.setMeetingLink(meetingLink);
        Session updatedSession = sessionRepository.save(session);
        log.info("Meeting link saved successfully for session: {}", sessionId);

        return mapSessionToResponse(updatedSession);
    }

    @Override
    public SessionResponse saveRecordingUrl(UUID sessionId, String recordingUrl) {
        log.debug("Saving recording URL for session: {}", sessionId);

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found with id: " + sessionId));

        if (recordingUrl == null || recordingUrl.isBlank()) {
            throw new BadRequestException("Recording URL cannot be empty");
        }

        if (session.getSessionStatus() != SessionStatus.COMPLETED) {
            throw new BadRequestException("Recording can only be saved for completed sessions");
        }

        session.setRecordingUrl(recordingUrl);
        Session updatedSession = sessionRepository.save(session);
        log.info("Recording URL saved successfully for session: {}", sessionId);

        return mapSessionToResponse(updatedSession);
    }

    private SessionResponse mapSessionToResponse(Session session) {
        return SessionResponse.builder()
                .id(session.getId())
                .bookingId(session.getBooking().getId())
                .meetingLink(session.getMeetingLink())
                .sessionStatus(session.getSessionStatus().name())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .recordingUrl(session.getRecordingUrl())
                .build();
    }
}
