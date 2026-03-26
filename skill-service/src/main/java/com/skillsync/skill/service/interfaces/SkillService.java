package com.skillsync.skill.service.interfaces;

import com.skillsync.skill.dto.SkillRequestDto;
import com.skillsync.skill.dto.SkillResponseDto;

import java.util.List;

public interface SkillService {
    SkillResponseDto createSkill(SkillRequestDto request);
    SkillResponseDto getSkillById(Long id);
    List<SkillResponseDto> getAllSkills();
    SkillResponseDto updateSkill(Long id, SkillRequestDto request);
    void deleteSkill(Long id);
}
