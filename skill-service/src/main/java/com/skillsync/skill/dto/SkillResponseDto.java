package com.skillsync.skill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SkillResponseDto {
    private Long id;
    private String name;
    private String category;
    private String description;
    private LocalDateTime createdAt;
}
