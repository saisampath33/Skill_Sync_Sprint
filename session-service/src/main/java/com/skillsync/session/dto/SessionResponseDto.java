package com.skillsync.session.dto;

import com.skillsync.session.entity.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SessionResponseDto {
    private Long id;
    private Long learnerId;
    private Long mentorId;
    private String mentorName;   // Feign enriched
    private Long skillId;
    private Session.SessionStatus status;
    private LocalDateTime scheduledAt;
    private Integer durationMin;
    private String notes;
}
