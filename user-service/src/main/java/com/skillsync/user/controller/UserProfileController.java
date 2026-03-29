package com.skillsync.user.controller;

import com.skillsync.user.dto.UserProfileRequestDto;
import com.skillsync.user.dto.UserProfileResponseDto;
import com.skillsync.user.service.interfaces.UserProfileService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Profiles", description = "User profile management")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Gateway injects X-User-Id header from JWT claims.
     */
    @PostMapping("/profile")
    @Operation(summary = "Create user profile")
    public ResponseEntity<UserProfileResponseDto> createProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userProfileService.createProfile(userId, request));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update own profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileRequestDto request) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    @GetMapping("/profile/me")
    @Operation(summary = "Get own profile")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(userProfileService.getProfileByUserId(userId));
    }

    @GetMapping("/profile/{userId}")
    @Operation(summary = "Get profile by userId (used by other services via Feign)")
    public ResponseEntity<UserProfileResponseDto> getProfile(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userProfileService.getProfileByUserId(userId));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all profiles – ADMIN only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponseDto>> getAllProfiles() {
        return ResponseEntity.ok(userProfileService.getAllProfiles());
    }

    @DeleteMapping("/profile/{userId}")
    @Operation(summary = "Delete profile – ADMIN only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteProfile(@PathVariable("userId") Long userId) {
        userProfileService.deleteProfile(userId);
        return ResponseEntity.ok(Map.of("message", "Profile deleted successfully"));
    }
}
