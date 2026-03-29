package com.skillsync.skill.mapper;

import com.skillsync.skill.dto.SkillResponseDto;
import com.skillsync.skill.entity.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {

    public SkillResponseDto toDto(Skill skill) {
        if (skill == null) return null;
        return SkillResponseDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .description(skill.getDescription())
                .createdAt(skill.getCreatedAt())
                .build();
    }
}
