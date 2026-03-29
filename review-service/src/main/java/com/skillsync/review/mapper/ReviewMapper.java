package com.skillsync.review.mapper;

import com.skillsync.review.dto.ReviewResponseDto;
import com.skillsync.review.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponseDto toDto(Review review) {
        if (review == null) return null;
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
