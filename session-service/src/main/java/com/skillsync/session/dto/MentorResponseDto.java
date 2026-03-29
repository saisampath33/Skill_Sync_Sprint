package com.skillsync.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MentorResponseDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String bio;
    private String status; // String to handle incoming enum "APPROVED"
    private Integer experience;
    private BigDecimal hourlyRate;
    private Double rating;
    private Integer totalReviews;
    private List<Long> skillIds;
    private LocalDateTime approvedAt;
}
