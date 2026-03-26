package com.skillsync.session.controller;

import com.skillsync.session.dto.SessionRequestDto;
import com.skillsync.session.dto.SessionResponseDto;
import com.skillsync.session.service.interfaces.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Tag(name = "Session Management", description = "Book, accept, reject, and manage coaching sessions")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    @Operation(summary = "Book a new session – LEARNER")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<SessionResponseDto> bookSession(
            @RequestHeader("X-User-Id") Long learnerId,
            @Valid @RequestBody SessionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.bookSession(learnerId, request));
    }

    @PutMapping("/{sessionId}/accept")
    @Operation(summary = "Accept a session request – MENTOR")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<SessionResponseDto> acceptSession(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(sessionService.acceptSession(mentorUserId, sessionId));
    }

    @PutMapping("/{sessionId}/reject")
    @Operation(summary = "Reject a session request – MENTOR")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<SessionResponseDto> rejectSession(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(sessionService.rejectSession(mentorUserId, sessionId));
    }

    @PutMapping("/{sessionId}/complete")
    @Operation(summary = "Mark session as completed – MENTOR")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<SessionResponseDto> completeSession(
            @RequestHeader("X-User-Id") Long mentorUserId,
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(sessionService.completeSession(mentorUserId, sessionId));
    }

    @PutMapping("/{sessionId}/cancel")
    @Operation(summary = "Cancel a session – LEARNER or MENTOR")
    public ResponseEntity<SessionResponseDto> cancelSession(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(sessionService.cancelSession(userId, sessionId));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session by ID")
    public ResponseEntity<SessionResponseDto> getSessionById(@PathVariable("sessionId") Long sessionId) {
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    @GetMapping("/learner")
    @Operation(summary = "Get all sessions for the logged-in learner")
    public ResponseEntity<List<SessionResponseDto>> getSessionsByLearner(
            @RequestHeader("X-User-Id") Long learnerId) {
        return ResponseEntity.ok(sessionService.getSessionsByLearner(learnerId));
    }

    @GetMapping("/mentor/{mentorId}")
    @Operation(summary = "Get all sessions for a specific mentor profile")
    public ResponseEntity<List<SessionResponseDto>> getSessionsByMentor(
            @PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok(sessionService.getSessionsByMentor(mentorId));
    }
}
