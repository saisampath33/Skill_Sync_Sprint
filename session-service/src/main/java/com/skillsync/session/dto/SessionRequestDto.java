package com.skillsync.session.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionRequestDto {

    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Schedule time is required")
    @Future(message = "Schedule time must be in the future")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Minimum duration is 15 minutes")
    private Integer durationMin;

    private String notes;
}
