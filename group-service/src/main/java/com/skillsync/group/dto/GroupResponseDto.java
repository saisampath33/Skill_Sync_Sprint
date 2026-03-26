package com.skillsync.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GroupResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long skillId;
    private String skillName; // Enriched via Feign later if needed
    private Long creatorId;
    private Integer maxMembers;
    private Integer currentMembersCount;
    private LocalDateTime createdAt;
    private List<Long> memberUserIds;
}
