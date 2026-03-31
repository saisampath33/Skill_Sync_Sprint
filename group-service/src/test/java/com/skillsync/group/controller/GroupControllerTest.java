package com.skillsync.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.group.dto.GroupResponseDto;
import com.skillsync.group.service.interfaces.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void createGroup_ReturnsCreated() throws Exception {
        GroupResponseDto response = GroupResponseDto.builder().id(1L).name("Java Circle").creatorId(10L).build();
        when(groupService.createGroup(org.mockito.ArgumentMatchers.eq(10L), ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/groups")
                        .header("X-User-Id", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupRequest("Java Circle", "Backend group", 5L, 10))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Java Circle"));

        verify(groupService).createGroup(org.mockito.ArgumentMatchers.eq(10L), ArgumentMatchers.any());
    }

    @Test
    void joinGroup_ReturnsOk() throws Exception {
        GroupResponseDto response = GroupResponseDto.builder().id(2L).name("Spring Team").build();
        when(groupService.joinGroup(12L, 2L)).thenReturn(response);

        mockMvc.perform(post("/groups/2/join").header("X-User-Id", 12L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spring Team"));

        verify(groupService).joinGroup(12L, 2L);
    }

    @Test
    void leaveGroup_ReturnsOk() throws Exception {
        doNothing().when(groupService).leaveGroup(13L, 3L);

        mockMvc.perform(post("/groups/3/leave").header("X-User-Id", 13L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Left group successfully"));

        verify(groupService).leaveGroup(13L, 3L);
    }

    @Test
    void getGroupById_ReturnsOk() throws Exception {
        when(groupService.getGroupById(4L)).thenReturn(GroupResponseDto.builder().id(4L).name("Algorithms").build());

        mockMvc.perform(get("/groups/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L));

        verify(groupService).getGroupById(4L);
    }

    @Test
    void getGroups_BySkillId_ReturnsOk() throws Exception {
        when(groupService.getGroupsBySkill(7L)).thenReturn(List.of(GroupResponseDto.builder().id(5L).name("Docker").build()));

        mockMvc.perform(get("/groups").param("skillId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Docker"));

        verify(groupService).getGroupsBySkill(7L);
    }

    @Test
    void getGroups_ByName_ReturnsOk() throws Exception {
        when(groupService.searchGroupsByName("cloud")).thenReturn(List.of(GroupResponseDto.builder().id(6L).name("Cloud Squad").build()));

        mockMvc.perform(get("/groups").param("name", "cloud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6L));

        verify(groupService).searchGroupsByName("cloud");
    }

    @Test
    void getGroups_All_ReturnsOk() throws Exception {
        when(groupService.getAllGroups()).thenReturn(List.of(GroupResponseDto.builder().id(7L).name("Testing").build()));

        mockMvc.perform(get("/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Testing"));

        verify(groupService).getAllGroups();
    }

    @Test
    void getMyGroups_ReturnsOk() throws Exception {
        when(groupService.getMyGroups(14L)).thenReturn(List.of(GroupResponseDto.builder().id(8L).name("My Group").build()));

        mockMvc.perform(get("/groups/my").header("X-User-Id", 14L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("My Group"));

        verify(groupService).getMyGroups(14L);
    }

    @Test
    void deleteGroup_ReturnsOk() throws Exception {
        doNothing().when(groupService).deleteGroup(15L, 9L);

        mockMvc.perform(delete("/groups/9").header("X-User-Id", 15L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Group deleted successfully"));

        verify(groupService).deleteGroup(15L, 9L);
    }

    private record GroupRequest(String name, String description, Long skillId, Integer maxMembers) {}
}
