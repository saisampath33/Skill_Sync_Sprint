package com.skillsync.review.service.impl;

import com.skillsync.review.dto.ReviewResponseDto;
import com.skillsync.review.entity.Review;
import com.skillsync.review.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void getReviewsByMentorId_Success() {
        Review review = Review.builder()
                .id(1L)
                .mentorId(10L)
                .learnerId(50L)
                .sessionId(100L)
                .rating(5)
                .comment("Great!")
                .build();

        when(reviewRepository.findByMentorId(10L)).thenReturn(List.of(review));

        List<ReviewResponseDto> response = reviewService.getReviewsByMentor(10L);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Great!", response.get(0).getComment());
    }
}
