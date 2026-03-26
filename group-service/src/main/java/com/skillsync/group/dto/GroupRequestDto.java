package com.skillsync.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupRequestDto {
    @NotBlank(message = "Group name is required")
    private String name;
    private String description;
    @NotNull(message = "Skill ID is required")
    private Long skillId;
    private Integer maxMembers;
}
