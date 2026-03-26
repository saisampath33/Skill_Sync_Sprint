package com.skillsync.review.service.impl;

import com.skillsync.review.dto.ReviewRequestDto;
import com.skillsync.review.dto.ReviewResponseDto;
import com.skillsync.review.entity.Review;
import com.skillsync.review.exception.BadRequestException;
import com.skillsync.review.exception.ResourceNotFoundException;
import com.skillsync.review.feign.MentorFeignClient;
import com.skillsync.review.repository.ReviewRepository;
import com.skillsync.review.service.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MentorFeignClient mentorFeignClient;

    @Override
    @Transactional
    @CacheEvict(value = "mentorReviews", key = "#request.mentorId")
    public ReviewResponseDto createReview(Long learnerId, ReviewRequestDto request) {
        // 1. Check if review for session already exists
        if (reviewRepository.existsBySessionId(request.getSessionId())) {
            throw new BadRequestException("Review already submitted for this session");
        }

        // 2. Save review
        Review review = Review.builder()
                .learnerId(learnerId)
                .mentorId(request.getMentorId())
                .sessionId(request.getSessionId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        Review saved = reviewRepository.save(review);

        // 3. Update mentor total rating via Feign
        Double avgRating = reviewRepository.getAverageRatingByMentorId(request.getMentorId());
        try {
            mentorFeignClient.updateMentorRating(request.getMentorId(), avgRating);
        } catch (Exception e) {
            log.error("Failed to update mentor rating: {}", e.getMessage());
            // In production, might use a retry or message queue
        }

        log.info("Review created for Session {} by Learner {}", request.getSessionId(), learnerId);
        return mapToDto(saved);
    }

    @Override
    @Cacheable(value = "mentorReviews", key = "#mentorId")
    public List<ReviewResponseDto> getReviewsByMentor(Long mentorId) {
        log.info("[CACHE MISS] Fetching reviews for mentor {} from DB", mentorId);
        return reviewRepository.findByMentorId(mentorId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDto> getReviewsByLearner(Long learnerId) {
        return reviewRepository.findByLearnerId(learnerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDto getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));
    }

    private ReviewResponseDto mapToDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .learnerId(review.getLearnerId())
                .mentorId(review.getMentorId())
                .sessionId(review.getSessionId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
