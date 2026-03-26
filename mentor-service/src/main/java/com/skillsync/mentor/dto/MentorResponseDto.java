package com.skillsync.mentor.dto;

import com.skillsync.mentor.entity.Mentor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MentorResponseDto {
    private Long id;
    private Long userId;
    private String fullName;     // enriched via Feign from User Service
    private String bio;
    private Mentor.MentorStatus status;
    private Integer experience;
    private BigDecimal hourlyRate;
    private Double rating;
    private Integer totalReviews;
    private List<Long> skillIds;
    private LocalDateTime approvedAt;
}
