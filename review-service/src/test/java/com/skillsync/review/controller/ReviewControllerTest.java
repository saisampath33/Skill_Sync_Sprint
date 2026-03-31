package com.skillsync.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.review.dto.ReviewResponseDto;
import com.skillsync.review.service.interfaces.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void createReview_ReturnsCreated() throws Exception {
        ReviewResponseDto response = ReviewResponseDto.builder().id(1L).mentorId(20L).rating(5).comment("Great session").build();
        when(reviewService.createReview(org.mockito.ArgumentMatchers.eq(30L), ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/reviews")
                        .header("X-User-Id", 30L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewRequest(20L, 40L, 5, "Great session"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mentorId").value(20L))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewService).createReview(org.mockito.ArgumentMatchers.eq(30L), ArgumentMatchers.any());
    }

    @Test
    void getReviewsByMentor_ReturnsOk() throws Exception {
        when(reviewService.getReviewsByMentor(21L)).thenReturn(List.of(ReviewResponseDto.builder().id(2L).comment("Helpful").build()));

        mockMvc.perform(get("/reviews/mentor/21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comment").value("Helpful"));

        verify(reviewService).getReviewsByMentor(21L);
    }

    @Test
    void getReviewsByLearner_ReturnsOk() throws Exception {
        when(reviewService.getReviewsByLearner(31L)).thenReturn(List.of(ReviewResponseDto.builder().id(3L).rating(4).build()));

        mockMvc.perform(get("/reviews/learner").header("X-User-Id", 31L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(4));

        verify(reviewService).getReviewsByLearner(31L);
    }

    @Test
    void getReviewById_ReturnsOk() throws Exception {
        when(reviewService.getReviewById(4L)).thenReturn(ReviewResponseDto.builder().id(4L).comment("Detailed").build());

        mockMvc.perform(get("/reviews/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Detailed"));

        verify(reviewService).getReviewById(4L);
    }

    private record ReviewRequest(Long mentorId, Long sessionId, Integer rating, String comment) {}
}
