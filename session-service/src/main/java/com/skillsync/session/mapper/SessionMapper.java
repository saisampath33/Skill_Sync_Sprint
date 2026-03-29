package com.skillsync.session.mapper;

import com.skillsync.session.dto.SessionResponseDto;
import com.skillsync.session.entity.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public SessionResponseDto toDto(Session session, String mentorName) {
        if (session == null) return null;
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
