package com.skillsync.mentor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorAvailability;
import com.skillsync.mentor.service.interfaces.MentorService;
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

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MentorControllerTest {

    @Mock
    private MentorService mentorService;

    @InjectMocks
    private MentorController mentorController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void applyAsMentor_ReturnsCreated() throws Exception {
        MentorResponseDto response = MentorResponseDto.builder()
                .id(1L)
                .userId(101L)
                .status(Mentor.MentorStatus.PENDING)
                .build();
        when(mentorService.applyAsMentor(org.mockito.ArgumentMatchers.eq(101L), org.mockito.ArgumentMatchers.eq("Jane Doe"), ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/mentors/apply")
                        .header("X-User-Id", 101L)
                        .header("X-User-Name", "Jane Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MentorApplicationRequest("I can mentor", "http://resume", 5, BigDecimal.valueOf(50), List.of(1L, 2L)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.userId").value(101L));

        verify(mentorService).applyAsMentor(org.mockito.ArgumentMatchers.eq(101L), org.mockito.ArgumentMatchers.eq("Jane Doe"), ArgumentMatchers.any());
    }

    @Test
    void approveMentor_ReturnsOk() throws Exception {
        when(mentorService.approveMentor(2L)).thenReturn(MentorResponseDto.builder().id(2L).status(Mentor.MentorStatus.APPROVED).build());

        mockMvc.perform(put("/mentors/2/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(mentorService).approveMentor(2L);
    }

    @Test
    void rejectMentor_ReturnsOk() throws Exception {
        when(mentorService.rejectMentor(3L)).thenReturn(MentorResponseDto.builder().id(3L).status(Mentor.MentorStatus.REJECTED).build());

        mockMvc.perform(put("/mentors/3/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(mentorService).rejectMentor(3L);
    }

    @Test
    void getMentorById_ReturnsOk() throws Exception {
        when(mentorService.getMentorById(4L)).thenReturn(MentorResponseDto.builder().id(4L).userId(104L).build());

        mockMvc.perform(get("/mentors/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(104L));

        verify(mentorService).getMentorById(4L);
    }

    @Test
    void getMyProfile_ReturnsOk() throws Exception {
        when(mentorService.getMentorByUserId(105L)).thenReturn(MentorResponseDto.builder().id(5L).userId(105L).build());

        mockMvc.perform(get("/mentors/my").header("X-User-Id", 105L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));

        verify(mentorService).getMentorByUserId(105L);
    }

    @Test
    void search_ReturnsOk() throws Exception {
        when(mentorService.searchMentors(8L, 4.5)).thenReturn(List.of(MentorResponseDto.builder().id(6L).userId(106L).build()));

        mockMvc.perform(get("/mentors/search")
                        .param("skillId", "8")
                        .param("minRating", "4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6L));

        verify(mentorService).searchMentors(8L, 4.5);
    }

    @Test
    void getAllApproved_ReturnsOk() throws Exception {
        when(mentorService.getAllApprovedMentors()).thenReturn(List.of(MentorResponseDto.builder().id(7L).build()));

        mockMvc.perform(get("/mentors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7L));

        verify(mentorService).getAllApprovedMentors();
    }

    @Test
    void addAvailability_ReturnsCreated() throws Exception {
        MentorAvailability availability = MentorAvailability.builder()
                .id(1L)
                .mentorId(8L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build();
        when(mentorService.addAvailability(org.mockito.ArgumentMatchers.eq(108L), org.mockito.ArgumentMatchers.eq(8L), ArgumentMatchers.any())).thenReturn(availability);

        mockMvc.perform(post("/mentors/8/availability")
                        .header("X-User-Id", 108L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(availability)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"));

        verify(mentorService).addAvailability(org.mockito.ArgumentMatchers.eq(108L), org.mockito.ArgumentMatchers.eq(8L), ArgumentMatchers.any());
    }

    @Test
    void getAvailability_ReturnsOk() throws Exception {
        MentorAvailability availability = MentorAvailability.builder()
                .id(2L)
                .mentorId(9L)
                .dayOfWeek(DayOfWeek.FRIDAY)
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
        when(mentorService.getAvailability(9L)).thenReturn(List.of(availability));

        mockMvc.perform(get("/mentors/9/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayOfWeek").value("FRIDAY"));

        verify(mentorService).getAvailability(9L);
    }

    @Test
    void updateRating_ReturnsOk() throws Exception {
        doNothing().when(mentorService).updateRating(10L, 4.8);

        mockMvc.perform(put("/mentors/10/rating").param("newRating", "4.8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Rating updated successfully"));

        verify(mentorService).updateRating(10L, 4.8);
    }

    private record MentorApplicationRequest(
            String motivation,
            String resumeUrl,
            Integer experience,
            BigDecimal hourlyRate,
            List<Long> skillIds
    ) {}
}
