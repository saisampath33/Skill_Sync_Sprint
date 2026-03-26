package com.skillsync.mentor.controller;

import com.skillsync.mentor.dto.MentorApplicationRequestDto;
import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.entity.MentorAvailability;
import com.skillsync.mentor.service.interfaces.MentorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
@Tag(name = "Mentor Management", description = "Mentor application, approval, profile, availability, search")
public class MentorController {

    private final MentorService mentorService;

    @PostMapping("/apply")
    @Operation(summary = "Apply to become a mentor")
    @PreAuthorize("hasAnyRole('LEARNER', 'MENTOR')")
    public ResponseEntity<MentorResponseDto> applyAsMentor(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String fullName,
            @RequestBody MentorApplicationRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorService.applyAsMentor(userId, fullName, request));
    }

    @PutMapping("/{mentorId}/approve")
    @Operation(summary = "Approve mentor – ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MentorResponseDto> approveMentor(@PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok(mentorService.approveMentor(mentorId));
    }

    @PutMapping("/{mentorId}/reject")
    @Operation(summary = "Reject mentor – ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MentorResponseDto> rejectMentor(@PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok(mentorService.rejectMentor(mentorId));
    }

    @GetMapping("/{mentorId}")
    @Operation(summary = "Get mentor by ID")
    public ResponseEntity<MentorResponseDto> getMentorById(@PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok(mentorService.getMentorById(mentorId));
    }

    @GetMapping("/my")
    @Operation(summary = "Get own mentor profile")
    public ResponseEntity<MentorResponseDto> getMyProfile(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(mentorService.getMentorByUserId(userId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search approved mentors by skillId and/or minRating – LEARNER")
    public ResponseEntity<List<MentorResponseDto>> search(
            @RequestParam(required = false) Long skillId,
            @RequestParam(required = false) Double minRating) {
        return ResponseEntity.ok(mentorService.searchMentors(skillId, minRating));
    }

    @GetMapping
    @Operation(summary = "Get all approved mentors")
    public ResponseEntity<List<MentorResponseDto>> getAllApproved() {
        return ResponseEntity.ok(mentorService.getAllApprovedMentors());
    }

    @PostMapping("/{mentorId}/availability")
    @Operation(summary = "Add availability slot – MENTOR")
    public ResponseEntity<MentorAvailability> addAvailability(
            @PathVariable("mentorId") Long mentorId,
            @RequestBody MentorAvailability availability) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorService.addAvailability(mentorId, availability));
    }

    @GetMapping("/{mentorId}/availability")
    @Operation(summary = "Get availability slots")
    public ResponseEntity<List<MentorAvailability>> getAvailability(@PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok(mentorService.getAvailability(mentorId));
    }

    @PutMapping("/{mentorId}/rating")
    @Operation(summary = "Update mentor rating – called internally by Review Service")
    public ResponseEntity<Map<String, String>> updateRating(
            @PathVariable("mentorId") Long mentorId,
            @RequestParam Double newRating) {
        mentorService.updateRating(mentorId, newRating);
        return ResponseEntity.ok(Map.of("message", "Rating updated successfully"));
    }
}
