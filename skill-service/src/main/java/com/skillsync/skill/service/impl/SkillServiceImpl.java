package com.skillsync.skill.service.impl;

import com.skillsync.skill.dto.SkillRequestDto;
import com.skillsync.skill.dto.SkillResponseDto;
import com.skillsync.skill.entity.Skill;
import com.skillsync.skill.exception.BadRequestException;
import com.skillsync.skill.exception.ResourceNotFoundException;
import com.skillsync.skill.mapper.SkillMapper;
import com.skillsync.skill.repository.SkillRepository;
import com.skillsync.skill.service.interfaces.SkillService;
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
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "skills", allEntries = true)
    })
    public SkillResponseDto createSkill(SkillRequestDto request) {
        if (skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Skill already exists: " + request.getName());
        }

        Skill skill = Skill.builder()
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .build();

        Skill saved = skillRepository.save(skill);
        log.info("Skill created: {}", saved.getName());
        return skillMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "skill", key = "#id")
    public SkillResponseDto getSkillById(Long id) {
        log.info("[CACHE MISS] Fetching skill {} from DB", id);
        return skillRepository.findById(id)
                .map(skillMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));
    }

    @Override
    @Cacheable(value = "skills", key = "'all'")
    public List<SkillResponseDto> getAllSkills() {
        log.info("[CACHE MISS] Fetching all skills from DB");
        return skillRepository.findAll().stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "skill", key = "#id"),
        @CacheEvict(value = "skills", allEntries = true)
    })
    public SkillResponseDto updateSkill(Long id, SkillRequestDto request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));

        if (!skill.getName().equalsIgnoreCase(request.getName()) &&
            skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Skill name already in use: " + request.getName());
        }

        skill.setName(request.getName());
        if (request.getCategory() != null) skill.setCategory(request.getCategory());
        if (request.getDescription() != null) skill.setDescription(request.getDescription());

        log.info("Skill updated: {}", skill.getId());
        return skillMapper.toDto(skillRepository.save(skill));
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "skill", key = "#id"),
        @CacheEvict(value = "skills", allEntries = true)
    })
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + id));
        skillRepository.delete(skill);
        log.info("Skill deleted: {}", id);
    }
}
