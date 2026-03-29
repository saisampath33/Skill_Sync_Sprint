package com.skillsync.group.service.impl;

import com.skillsync.group.dto.GroupRequestDto;
import com.skillsync.group.dto.GroupResponseDto;
import com.skillsync.group.entity.Group;
import com.skillsync.group.entity.GroupMember;
import com.skillsync.group.exception.BadRequestException;
import com.skillsync.group.exception.ResourceNotFoundException;
import com.skillsync.group.feign.SkillFeignClient;
import com.skillsync.group.mapper.GroupMapper;
import com.skillsync.group.repository.GroupMemberRepository;
import com.skillsync.group.repository.GroupRepository;
import com.skillsync.group.service.interfaces.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SkillFeignClient skillFeignClient;
    private final GroupMapper groupMapper;

    @Override
    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    public GroupResponseDto createGroup(Long creatorId, GroupRequestDto request) {
        // 1. Verify skill exists
        try {
            skillFeignClient.getSkillById(request.getSkillId());
        } catch (Exception e) {
            log.error("Skill validation failed for ID {}: {}", request.getSkillId(), e.getMessage());
            throw new ResourceNotFoundException("Skill not found with ID: " + request.getSkillId());
        }

        // 2. Save group
        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .skillId(request.getSkillId())
                .creatorId(creatorId)
                .maxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 10)
                .build();
        Group savedGroup = groupRepository.save(group);

        // 3. Add creator as ADMIN member
        GroupMember admin = GroupMember.builder()
                .groupId(savedGroup.getId())
                .userId(creatorId)
                .role(GroupMember.MemberRole.ADMIN)
                .build();
        groupMemberRepository.save(admin);

        log.info("Group '{}' created by User {}", savedGroup.getName(), creatorId);
        return enrichAndMap(savedGroup);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "group", key = "#groupId"),
        @CacheEvict(value = "groups", allEntries = true)
    })
    public GroupResponseDto joinGroup(Long userId, Long groupId) {
        Group group = getGroupEntity(groupId);

        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new BadRequestException("User is already a member of this group");
        }

        int currentCount = groupMemberRepository.countByGroupId(groupId);
        if (currentCount >= group.getMaxMembers()) {
            throw new BadRequestException("Group has reached its maximum member capacity");
        }

        GroupMember member = GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .role(GroupMember.MemberRole.LEARNER)
                .build();
        groupMemberRepository.save(member);

        log.info("User {} joined Group {}", userId, group.getName());
        return enrichAndMap(group);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "group", key = "#groupId"),
        @CacheEvict(value = "groups", allEntries = true)
    })
    public void leaveGroup(Long userId, Long groupId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this group"));

        groupMemberRepository.delete(member);
        log.info("User {} left Group {}", userId, groupId);
    }

    @Override
    @Cacheable(value = "group", key = "#id")
    public GroupResponseDto getGroupById(Long id) {
        log.info("[CACHE MISS] Fetching group {} from DB", id);
        return enrichAndMap(getGroupEntity(id));
    }

    @Override
    @Cacheable(value = "groups", key = "'all'")
    public List<GroupResponseDto> getAllGroups() {
        log.info("[CACHE MISS] Fetching all groups from DB");
        return groupRepository.findAll().stream()
                .map(this::enrichAndMap)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupResponseDto> getGroupsBySkill(Long skillId) {
        return groupRepository.findBySkillId(skillId).stream()
                .map(this::enrichAndMap)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupResponseDto> searchGroupsByName(String name) {
        return groupRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::enrichAndMap)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupResponseDto> getMyGroups(Long userId) {
        List<Long> groupIds = groupMemberRepository.findByUserId(userId).stream()
                .map(GroupMember::getGroupId)
                .collect(Collectors.toList());
        
        return groupRepository.findAllById(groupIds).stream()
                .map(this::enrichAndMap)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "group", key = "#groupId"),
        @CacheEvict(value = "groups", allEntries = true)
    })
    public void deleteGroup(Long creatorId, Long groupId) {
        Group group = getGroupEntity(groupId);
        if (!group.getCreatorId().equals(creatorId)) {
            throw new BadRequestException("Only the group creator can delete the group");
        }

        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        groupMemberRepository.deleteAll(members);
        groupRepository.delete(group);
        log.info("Group {} deleted by Creator {}", groupId, creatorId);
    }

    // --- Helpers ---

    private Group getGroupEntity(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
    }

    private GroupResponseDto enrichAndMap(Group group) {
        List<Long> members = groupMemberRepository.findByGroupId(group.getId()).stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toList());

        String skillName = "Unknown Skill";
        try {
            skillName = skillFeignClient.getSkillById(group.getSkillId()).getName();
        } catch (Exception e) {
             log.warn("Could not fetch skill name for ID {}: {}", group.getSkillId(), e.getMessage());
        }

        return groupMapper.toDto(group, skillName, members);
    }
}
