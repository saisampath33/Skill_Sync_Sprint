package com.skillsync.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Long learnerId;
    private Long mentorId;
    private Long sessionId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
