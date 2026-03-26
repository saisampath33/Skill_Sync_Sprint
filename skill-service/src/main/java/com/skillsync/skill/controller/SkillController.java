package com.skillsync.skill.controller;

import com.skillsync.skill.dto.SkillRequestDto;
import com.skillsync.skill.dto.SkillResponseDto;
import com.skillsync.skill.service.interfaces.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@Tag(name = "Skill Catalog", description = "Manage system-wide skills")
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    @Operation(summary = "Create a new skill – ADMIN only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillResponseDto> createSkill(@Valid @RequestBody SkillRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(request));
    }

    @GetMapping
    @Operation(summary = "Get all skills – Public")
    public ResponseEntity<List<SkillResponseDto>> getAllSkills() {

        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get skill by ID – Public")
    public ResponseEntity<SkillResponseDto> getSkillById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update skill – ADMIN only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillResponseDto> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillRequestDto request) {
        return ResponseEntity.ok(skillService.updateSkill(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete skill – ADMIN only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(Map.of("message", "Skill deleted successfully"));
    }
}
