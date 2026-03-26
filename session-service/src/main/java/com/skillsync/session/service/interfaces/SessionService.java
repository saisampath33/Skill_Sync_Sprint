package com.skillsync.session.service.interfaces;

import com.skillsync.session.dto.SessionRequestDto;
import com.skillsync.session.dto.SessionResponseDto;

import java.util.List;

public interface SessionService {
    SessionResponseDto bookSession(Long learnerId, SessionRequestDto request);
    SessionResponseDto acceptSession(Long mentorId, Long sessionId);
    SessionResponseDto rejectSession(Long mentorId, Long sessionId);
    SessionResponseDto completeSession(Long mentorId, Long sessionId);
    SessionResponseDto cancelSession(Long userId, Long sessionId); // both can cancel

    SessionResponseDto getSessionById(Long sessionId);
    List<SessionResponseDto> getSessionsByLearner(Long learnerId);
    List<SessionResponseDto> getSessionsByMentor(Long mentorId);
}
