package com.skillsync.review.service.interfaces;

import com.skillsync.review.dto.ReviewRequestDto;
import com.skillsync.review.dto.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto createReview(Long learnerId, ReviewRequestDto request);
    List<ReviewResponseDto> getReviewsByMentor(Long mentorId);
    List<ReviewResponseDto> getReviewsByLearner(Long learnerId);
    ReviewResponseDto getReviewById(Long id);
}
