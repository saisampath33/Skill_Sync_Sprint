package com.skillsync.session.service.impl;

import com.skillsync.session.dto.MentorResponseDto;
import com.skillsync.session.dto.SessionRequestDto;
import com.skillsync.session.dto.SessionResponseDto;
import com.skillsync.session.entity.Session;
import com.skillsync.session.exception.BadRequestException;
import com.skillsync.session.exception.ResourceNotFoundException;
import com.skillsync.session.feign.MentorFeignClient;
import com.skillsync.session.mapper.SessionMapper;
import com.skillsync.session.rabbitmq.SessionEventPublisher;
import com.skillsync.session.repository.SessionRepository;
import com.skillsync.session.service.interfaces.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    private final SessionMapper sessionMapper;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "mySessions", key = "'learner_' + #learnerId")
    })
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
        return sessionMapper.toDto(saved, mentor.getFullName());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "session", key = "#sessionId"),
        @CacheEvict(value = "mySessions", allEntries = true)
    })
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

        return sessionMapper.toDto(saved, mentor.getFullName());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "session", key = "#sessionId"),
        @CacheEvict(value = "mySessions", allEntries = true)
    })
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

        return sessionMapper.toDto(saved, mentor.getFullName());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "session", key = "#sessionId"),
        @CacheEvict(value = "mySessions", allEntries = true)
    })
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
        return sessionMapper.toDto(sessionRepository.save(session), mentor.getFullName());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "session", key = "#sessionId"),
        @CacheEvict(value = "mySessions", allEntries = true)
    })
    public SessionResponseDto cancelSession(Long userId, Long sessionId) {
        Session session = getSessionByIdEntity(sessionId);

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
        return sessionMapper.toDto(saved, mentor != null ? mentor.getFullName() : null);
    }

    @Override
    @Cacheable(value = "session", key = "#sessionId")
    public SessionResponseDto getSessionById(Long sessionId) {
        log.info("[CACHE MISS] Fetching session {} from DB", sessionId);
        Session session = getSessionByIdEntity(sessionId);
        String mentorName = fetchMentorNameSafely(session.getMentorId());
        return sessionMapper.toDto(session, mentorName);
    }

    @Override
    @Cacheable(value = "mySessions", key = "'learner_' + #learnerId")
    public List<SessionResponseDto> getSessionsByLearner(Long learnerId) {
        log.info("[CACHE MISS] Fetching sessions for learner {} from DB", learnerId);
        return sessionRepository.findByLearnerId(learnerId).stream()
                .map(s -> sessionMapper.toDto(s, fetchMentorNameSafely(s.getMentorId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionResponseDto> getSessionsByMentor(Long mentorId) {
        return sessionRepository.findByMentorId(mentorId).stream()
                .map(s -> sessionMapper.toDto(s, fetchMentorNameSafely(s.getMentorId())))
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
}
