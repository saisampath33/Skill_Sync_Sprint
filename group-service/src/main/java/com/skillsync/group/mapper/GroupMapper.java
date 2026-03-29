package com.skillsync.group.mapper;

import com.skillsync.group.dto.GroupResponseDto;
import com.skillsync.group.entity.Group;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupMapper {

    public GroupResponseDto toDto(Group group, String skillName, List<Long> memberUserIds) {
        if (group == null) return null;
        return GroupResponseDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .skillId(group.getSkillId())
                .skillName(skillName)
                .creatorId(group.getCreatorId())
                .maxMembers(group.getMaxMembers())
                .currentMembersCount(memberUserIds != null ? memberUserIds.size() : 0)
                .createdAt(group.getCreatedAt())
                .memberUserIds(memberUserIds)
                .build();
    }
}
