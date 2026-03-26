package com.skillsync.group.service.impl;

import com.skillsync.group.dto.GroupResponseDto;
import com.skillsync.group.entity.Group;
import com.skillsync.group.repository.GroupRepository;
import com.skillsync.group.repository.GroupMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;
    
    @Mock
    private GroupMemberRepository groupMemberRepository;

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

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(groupMemberRepository.findByGroupId(1L)).thenReturn(List.of());

        GroupResponseDto response = groupService.getGroupById(1L);

        assertNotNull(response);
        assertEquals("Java Study", response.getName());
        assertEquals(10L, response.getCreatorId());
    }
}
