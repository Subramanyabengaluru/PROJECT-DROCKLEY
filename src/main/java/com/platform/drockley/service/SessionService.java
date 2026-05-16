package com.platform.drockley.service;

import com.platform.drockley.common.dto.SessionResponse;

import java.util.UUID;

/**
 * Service interface for session management operations.
 */
public interface SessionService {

    /**
     * Creates a new session for a booking.
     *
     * @param bookingId the ID of the booking to create a session for
     * @return the created session response
     */
    SessionResponse createSession(UUID bookingId);

    /**
     * Starts an existing session.
     *
     * @param sessionId the ID of the session to start
     * @return the updated session response
     */
    SessionResponse startSession(UUID sessionId);

    /**
     * Ends a session.
     *
     * @param sessionId the ID of the session to end
     * @return the updated session response
     */
    SessionResponse endSession(UUID sessionId);

    /**
     * Saves the meeting link for a session.
     *
     * @param sessionId the ID of the session
     * @param meetingLink the meeting link URL
     * @return the updated session response
     */
    SessionResponse saveMeetingLink(UUID sessionId, String meetingLink);

    /**
     * Saves the recording URL for a completed session.
     *
     * @param sessionId the ID of the session
     * @param recordingUrl the recording URL
     * @return the updated session response
     */
    SessionResponse saveRecordingUrl(UUID sessionId, String recordingUrl);
}
