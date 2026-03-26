package com.skillsync.auth.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorEventDto {
    private Long mentorId;
    private Long userId;
    private String eventType;
    private LocalDateTime timestamp;
}
