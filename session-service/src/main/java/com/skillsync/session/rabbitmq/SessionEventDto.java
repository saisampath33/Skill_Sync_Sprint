package com.skillsync.session.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SessionEventDto {
    private Long sessionId;
    private Long learnerId;
    private Long mentorId;
    private String eventType;
    private String scheduledAt;
    private LocalDateTime timestamp;
}
