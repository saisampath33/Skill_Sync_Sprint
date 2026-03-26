package com.skillsync.session.service.impl;

import com.skillsync.session.dto.MentorResponseDto;
import com.skillsync.session.dto.SessionRequestDto;
import com.skillsync.session.dto.SessionResponseDto;
import com.skillsync.session.entity.Session;
import com.skillsync.session.exception.BadRequestException;
import com.skillsync.session.exception.ResourceNotFoundException;
import com.skillsync.session.feign.MentorFeignClient;
import com.skillsync.session.rabbitmq.SessionEventPublisher;
import com.skillsync.session.repository.SessionRepository;
import com.skillsync.session.service.interfaces.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final MentorFeignClient mentorFeignClient;
    private final SessionEventPublisher eventPublisher;

    @Override
    @Transactional
    public SessionResponseDto bookSession(Long learnerId, SessionRequestDto request) {
        // 1. Verify mentor is approved
        MentorResponseDto mentor = mentorFeignClient.getMentorById(request.getMentorId());
        if (mentor == null || !"APPROVED".equals(mentor.getStatus())) {
            throw new BadRequestException("Mentor is not available or not approved.");
        }

        // 2. Create session
        Session session = Session.builder()
                .learnerId(learnerId)
                .mentorId(request.getMentorId())
                .skillId(request.getSkillId())
                .scheduledAt(request.getScheduledAt())
                .durationMin(request.getDurationMin())
                .notes(request.getNotes())
                .status(Session.SessionStatus.REQUESTED)
                .build();
        Session saved = sessionRepository.save(session);

        // 3. Publish event
        eventPublisher.publishSessionBooked(saved.getId(), learnerId, request.getMentorId(), saved.getScheduledAt().toString());

        log.info("Session {} booked by Learner {} with Mentor {}", saved.getId(), learnerId, request.getMentorId());
        return mapToDto(saved, mentor.getFullName());
    }

    @Override
    @Transactional
    public SessionResponseDto acceptSession(Long mentorUserId, Long sessionId) {
        Session session = getSessionByIdEntity(sessionId);
        
        MentorResponseDto mentor = mentorFeignClient.getMentorById(session.getMentorId());
        if (!mentor.getUserId().equals(mentorUserId)) {
            throw new BadRequestException("Only the assigned mentor can accept this session");
        }

        if (session.getStatus() != Session.SessionStatus.REQUESTED) {
            throw new BadRequestException("Session is not in REQUESTED state");
        }

        session.setStatus(Session.SessionStatus.ACCEPTED);
        Session saved = sessionRepository.save(session);

        eventPublisher.publishSessionAccepted(saved.getId(), saved.getLearnerId(), saved.getMentorId(), saved.getScheduledAt().toString());

        return mapToDto(saved, mentor.getFullName());
    }

    @Override
    @Transactional
    public SessionResponseDto rejectSession(Long mentorUserId, Long sessionId) {
        Session session = getSessionByIdEntity(sessionId);

        MentorResponseDto mentor = mentorFeignClient.getMentorById(session.getMentorId());
        if (!mentor.getUserId().equals(mentorUserId)) {
            throw new BadRequestException("Only the assigned mentor can reject this session");
        }

        if (session.getStatus() != Session.SessionStatus.REQUESTED) {
            throw new BadRequestException("Session is not in REQUESTED state");
        }

        session.setStatus(Session.SessionStatus.REJECTED);
        Session saved = sessionRepository.save(session);

        eventPublisher.publishSessionRejected(saved.getId(), saved.getLearnerId(), saved.getMentorId(), saved.getScheduledAt().toString());

        return mapToDto(saved, mentor.getFullName());
    }

    @Override
    @Transactional
    public SessionResponseDto completeSession(Long mentorUserId, Long sessionId) {
        Session session = getSessionByIdEntity(sessionId);

        MentorResponseDto mentor = mentorFeignClient.getMentorById(session.getMentorId());
        if (!mentor.getUserId().equals(mentorUserId)) {
            throw new BadRequestException("Only the assigned mentor can complete this session");
        }

        if (session.getStatus() != Session.SessionStatus.ACCEPTED) {
            throw new BadRequestException("Only ACCEPTED sessions can be marked as COMPLETED");
        }

        session.setStatus(Session.SessionStatus.COMPLETED);
        return mapToDto(sessionRepository.save(session), mentor.getFullName());
    }

    @Override
    @Transactional
    public SessionResponseDto cancelSession(Long userId, Long sessionId) {
        Session session = getSessionByIdEntity(sessionId);

        // Allow learner or mentor to cancel
        MentorResponseDto mentor = null;
        try {
            mentor = mentorFeignClient.getMentorById(session.getMentorId());
        } catch (Exception e) {
            log.warn("Mentor could not be fetched for session {}", sessionId);
        }

        boolean isLearner = session.getLearnerId().equals(userId);
        boolean isMentor = mentor != null && mentor.getUserId().equals(userId);

        if (!isLearner && !isMentor) {
            throw new BadRequestException("Only the participant learner or mentor can cancel this session");
        }

        if (session.getStatus() == Session.SessionStatus.COMPLETED || session.getStatus() == Session.SessionStatus.REJECTED) {
            throw new BadRequestException("Cannot cancel a completed or rejected session");
        }

        session.setStatus(Session.SessionStatus.CANCELLED);
        Session saved = sessionRepository.save(session);
        return mapToDto(saved, mentor != null ? mentor.getFullName() : null);
    }

    @Override
    public SessionResponseDto getSessionById(Long sessionId) {
        Session session = getSessionByIdEntity(sessionId);
        String mentorName = fetchMentorNameSafely(session.getMentorId());
        return mapToDto(session, mentorName);
    }

    @Override
    public List<SessionResponseDto> getSessionsByLearner(Long learnerId) {
        return sessionRepository.findByLearnerId(learnerId).stream()
                .map(s -> mapToDto(s, fetchMentorNameSafely(s.getMentorId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionResponseDto> getSessionsByMentor(Long mentorId) {
        return sessionRepository.findByMentorId(mentorId).stream()
                .map(s -> mapToDto(s, fetchMentorNameSafely(s.getMentorId())))
                .collect(Collectors.toList());
    }

    // --- Helpers ---

    private Session getSessionByIdEntity(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));
    }

    private String fetchMentorNameSafely(Long mentorId) {
        try {
            return mentorFeignClient.getMentorById(mentorId).getFullName();
        } catch (Exception e) {
            return "Unknown Mentor";
        }
    }

    private SessionResponseDto mapToDto(Session session, String mentorName) {
        return SessionResponseDto.builder()
                .id(session.getId())
                .learnerId(session.getLearnerId())
                .mentorId(session.getMentorId())
                .mentorName(mentorName)
                .skillId(session.getSkillId())
                .status(session.getStatus())
                .scheduledAt(session.getScheduledAt())
                .durationMin(session.getDurationMin())
                .notes(session.getNotes())
                .build();
    }
}
