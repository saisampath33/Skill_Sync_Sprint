package com.skillsync.group.controller;

import com.skillsync.group.dto.GroupRequestDto;
import com.skillsync.group.dto.GroupResponseDto;
import com.skillsync.group.service.interfaces.GroupService;
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
@RequestMapping("/groups")
@RequiredArgsConstructor
@Tag(name = "Peer Learning Groups", description = "Create and manage peer learning groups")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @Operation(summary = "Create a new peer learning group")
    @PreAuthorize("hasAnyRole('LEARNER', 'MENTOR')")
    public ResponseEntity<GroupResponseDto> createGroup(
            @RequestHeader("X-User-Id") Long creatorId,
            @Valid @RequestBody GroupRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupService.createGroup(creatorId, request));
    }

    @PostMapping("/{groupId}/join")
    @Operation(summary = "Join a peer group")
    @PreAuthorize("hasAnyRole('LEARNER', 'MENTOR')")
    public ResponseEntity<GroupResponseDto> joinGroup(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("groupId") Long groupId) {
        return ResponseEntity.ok(groupService.joinGroup(userId, groupId));
    }

    @PostMapping("/{groupId}/leave")
    @Operation(summary = "Leave a peer group")
    @PreAuthorize("hasAnyRole('LEARNER', 'MENTOR')")
    public ResponseEntity<Map<String, String>> leaveGroup(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("groupId") Long groupId) {
        groupService.leaveGroup(userId, groupId);
        return ResponseEntity.ok(Map.of("message", "Left group successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get group details by ID")
    public ResponseEntity<GroupResponseDto> getGroupById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping
    @Operation(summary = "Get all peer groups or filter by skill/name")
    public ResponseEntity<List<GroupResponseDto>> getGroups(
            @RequestParam(name = "skillId", required = false) Long skillId,
            @RequestParam(name = "name", required = false) String name) {
        if (skillId != null) {
            return ResponseEntity.ok(groupService.getGroupsBySkill(skillId));
        } else if (name != null) {
            return ResponseEntity.ok(groupService.searchGroupsByName(name));
        }
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/my")
    @Operation(summary = "Get groups the current user is a member of")
    public ResponseEntity<List<GroupResponseDto>> getMyGroups(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(groupService.getMyGroups(userId));
    }

    @DeleteMapping("/{groupId}")
    @Operation(summary = "Delete a group (Creator or Admin only)")
    @PreAuthorize("hasAnyRole('LEARNER', 'MENTOR', 'ADMIN')")
    public ResponseEntity<Map<String, String>> deleteGroup(
            @RequestHeader("X-User-Id") Long creatorId,
            @PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(creatorId, groupId);
        return ResponseEntity.ok(Map.of("message", "Group deleted successfully"));
    }
}
