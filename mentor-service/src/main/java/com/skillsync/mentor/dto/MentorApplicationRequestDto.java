package com.skillsync.mentor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MentorApplicationRequestDto {
    private String motivation;
    private String resumeUrl;
    @NotNull private Integer experience;
    @NotNull private BigDecimal hourlyRate;
    private List<Long> skillIds;
}
