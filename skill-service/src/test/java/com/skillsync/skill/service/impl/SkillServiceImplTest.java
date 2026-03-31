package com.skillsync.skill.service.impl;

import com.skillsync.skill.dto.SkillRequestDto;
import com.skillsync.skill.dto.SkillResponseDto;
import com.skillsync.skill.entity.Skill;
import com.skillsync.skill.exception.BadRequestException;
import com.skillsync.skill.exception.ResourceNotFoundException;
import com.skillsync.skill.mapper.SkillMapper;
import com.skillsync.skill.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;

    private Skill skill;
    private SkillRequestDto requestDto;
    private SkillResponseDto responseDto;

    @BeforeEach
    void setUp() {
        skill = Skill.builder()
                .id(1L)
                .name("Java")
                .category("Backend")
                .description("Java programming")
                .build();

        requestDto = new SkillRequestDto();
        requestDto.setName("Java");
        requestDto.setCategory("Backend");
        requestDto.setDescription("Java programming");

        responseDto = SkillResponseDto.builder()
                .id(1L)
                .name("Java")
                .build();
    }

    @Test
    void createSkill_Success() {
        when(skillRepository.existsByNameIgnoreCase("Java")).thenReturn(false);
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);
        when(skillMapper.toDto(any(Skill.class))).thenReturn(responseDto);

        SkillResponseDto response = skillService.createSkill(requestDto);

        assertNotNull(response);
        assertEquals("Java", response.getName());
        verify(skillRepository, times(1)).save(any(Skill.class));
    }

    @Test
    void createSkill_ThrowsBadRequest_WhenExists() {
        when(skillRepository.existsByNameIgnoreCase("Java")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> 
            skillService.createSkill(requestDto)
        );
        verify(skillRepository, never()).save(any(Skill.class));
    }

    @Test
    void getSkillById_Success() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillMapper.toDto(skill)).thenReturn(responseDto);

        SkillResponseDto response = skillService.getSkillById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getSkillById_ThrowsNotFound() {
        when(skillRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            skillService.getSkillById(999L)
        );
    }
}
