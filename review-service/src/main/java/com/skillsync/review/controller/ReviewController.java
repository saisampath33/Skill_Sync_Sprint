package com.skillsync.review.controller;

import com.skillsync.review.dto.ReviewRequestDto;
import com.skillsync.review.dto.ReviewResponseDto;
import com.skillsync.review.service.interfaces.ReviewService;
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
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Mentor Reviews", description = "Submit and view mentor ratings and feedback")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Submit a review for a completed session")
    @PreAuthorize("hasAnyRole('LEARNER', 'ADMIN')")
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestHeader("X-User-Id") Long learnerId,
            @Valid @RequestBody ReviewRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(learnerId, request));
    }

    @GetMapping("/mentor/{mentorId}")
    @Operation(summary = "Get all reviews for a mentor")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByMentor(@PathVariable("mentorId") Long mentorId) {
        return ResponseEntity.ok(reviewService.getReviewsByMentor(mentorId));
    }

    @GetMapping("/learner")
    @Operation(summary = "Get all reviews submitted by the current user")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByLearner(
            @RequestHeader("X-User-Id") Long learnerId) {
        return ResponseEntity.ok(reviewService.getReviewsByLearner(learnerId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review details by ID")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }
}
