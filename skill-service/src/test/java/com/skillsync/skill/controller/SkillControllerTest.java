package com.skillsync.skill.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.skill.dto.SkillResponseDto;
import com.skillsync.skill.service.interfaces.SkillService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(skillController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void createSkill_ReturnsCreated() throws Exception {
        when(skillService.createSkill(ArgumentMatchers.any())).thenReturn(SkillResponseDto.builder().id(1L).name("Java").build());

        mockMvc.perform(post("/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SkillRequest("Java", "Backend", "Programming language"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Java"));

        verify(skillService).createSkill(ArgumentMatchers.any());
    }

    @Test
    void getAllSkills_ReturnsOk() throws Exception {
        when(skillService.getAllSkills()).thenReturn(List.of(SkillResponseDto.builder().id(2L).name("Spring").build()));

        mockMvc.perform(get("/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));

        verify(skillService).getAllSkills();
    }

    @Test
    void getSkillById_ReturnsOk() throws Exception {
        when(skillService.getSkillById(3L)).thenReturn(SkillResponseDto.builder().id(3L).name("Docker").build());

        mockMvc.perform(get("/skills/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Docker"));

        verify(skillService).getSkillById(3L);
    }

    @Test
    void updateSkill_ReturnsOk() throws Exception {
        when(skillService.updateSkill(org.mockito.ArgumentMatchers.eq(4L), ArgumentMatchers.any())).thenReturn(SkillResponseDto.builder().id(4L).name("Kubernetes").build());

        mockMvc.perform(put("/skills/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SkillRequest("Kubernetes", "DevOps", "Container orchestration"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L));

        verify(skillService).updateSkill(org.mockito.ArgumentMatchers.eq(4L), ArgumentMatchers.any());
    }

    @Test
    void deleteSkill_ReturnsOk() throws Exception {
        doNothing().when(skillService).deleteSkill(5L);

        mockMvc.perform(delete("/skills/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Skill deleted successfully"));

        verify(skillService).deleteSkill(5L);
    }

    private record SkillRequest(String name, String category, String description) {}
}
