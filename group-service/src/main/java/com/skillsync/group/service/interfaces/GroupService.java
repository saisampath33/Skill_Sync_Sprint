package com.skillsync.group.service.interfaces;

import com.skillsync.group.dto.GroupRequestDto;
import com.skillsync.group.dto.GroupResponseDto;

import java.util.List;

public interface GroupService {
    GroupResponseDto createGroup(Long creatorId, GroupRequestDto request);
    GroupResponseDto joinGroup(Long userId, Long groupId);
    void leaveGroup(Long userId, Long groupId);
    GroupResponseDto getGroupById(Long id);
    List<GroupResponseDto> getAllGroups();
    List<GroupResponseDto> getGroupsBySkill(Long skillId);
    List<GroupResponseDto> searchGroupsByName(String name);
    List<GroupResponseDto> getMyGroups(Long userId);
    void deleteGroup(Long creatorId, Long groupId);
}
