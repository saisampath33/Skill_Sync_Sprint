package com.skillsync.group.service.impl;

import com.skillsync.group.dto.GroupResponseDto;
import com.skillsync.group.entity.Group;
import com.skillsync.group.feign.SkillFeignClient;
import com.skillsync.group.mapper.GroupMapper;
import com.skillsync.group.repository.GroupMemberRepository;
import com.skillsync.group.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;
    
    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private SkillFeignClient skillFeignClient;

    @Mock
    private GroupMapper groupMapper;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    void getGroupById_Success() {
        Group group = Group.builder()
                .id(1L)
                .name("Java Study")
                .creatorId(10L)
                .skillId(5L)
                .build();

        GroupResponseDto responseDto = GroupResponseDto.builder()
                .id(1L)
                .name("Java Study")
                .creatorId(10L)
                .skillName("Java")
                .build();
        SkillFeignClient.SkillResponseDto skillResponseDto = new SkillFeignClient.SkillResponseDto();
        skillResponseDto.setId(5L);
        skillResponseDto.setName("Java");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByGroupId(1L)).thenReturn(List.of());
        when(skillFeignClient.getSkillById(5L)).thenReturn(skillResponseDto);
        when(groupMapper.toDto(group, "Java", List.of())).thenReturn(responseDto);

        GroupResponseDto response = groupService.getGroupById(1L);

        assertNotNull(response);
        assertEquals("Java Study", response.getName());
        assertEquals(10L, response.getCreatorId());
    }
}
