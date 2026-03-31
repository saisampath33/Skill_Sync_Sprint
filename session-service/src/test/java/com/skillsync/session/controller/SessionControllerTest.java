package com.skillsync.session.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.session.dto.SessionResponseDto;
import com.skillsync.session.entity.Session;
import com.skillsync.session.service.interfaces.SessionService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private SessionController sessionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void bookSession_ReturnsCreated() throws Exception {
        SessionResponseDto response = SessionResponseDto.builder()
                .id(1L)
                .learnerId(50L)
                .mentorId(60L)
                .status(Session.SessionStatus.REQUESTED)
                .build();
        when(sessionService.bookSession(org.mockito.ArgumentMatchers.eq(50L), ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(post("/sessions")
                        .header("X-User-Id", 50L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SessionRequest(60L, 5L, LocalDateTime.now().plusDays(1), 45, "Need help with Spring"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REQUESTED"))
                .andExpect(jsonPath("$.mentorId").value(60L));

        verify(sessionService).bookSession(org.mockito.ArgumentMatchers.eq(50L), ArgumentMatchers.any());
    }

    @Test
    void acceptSession_ReturnsOk() throws Exception {
        when(sessionService.acceptSession(61L, 2L)).thenReturn(SessionResponseDto.builder().id(2L).status(Session.SessionStatus.ACCEPTED).build());

        mockMvc.perform(put("/sessions/2/accept").header("X-User-Id", 61L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(sessionService).acceptSession(61L, 2L);
    }

    @Test
    void rejectSession_ReturnsOk() throws Exception {
        when(sessionService.rejectSession(62L, 3L)).thenReturn(SessionResponseDto.builder().id(3L).status(Session.SessionStatus.REJECTED).build());

        mockMvc.perform(put("/sessions/3/reject").header("X-User-Id", 62L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(sessionService).rejectSession(62L, 3L);
    }

    @Test
    void completeSession_ReturnsOk() throws Exception {
        when(sessionService.completeSession(63L, 4L)).thenReturn(SessionResponseDto.builder().id(4L).status(Session.SessionStatus.COMPLETED).build());

        mockMvc.perform(put("/sessions/4/complete").header("X-User-Id", 63L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(sessionService).completeSession(63L, 4L);
    }

    @Test
    void cancelSession_ReturnsOk() throws Exception {
        when(sessionService.cancelSession(64L, 5L)).thenReturn(SessionResponseDto.builder().id(5L).status(Session.SessionStatus.CANCELLED).build());

        mockMvc.perform(put("/sessions/5/cancel").header("X-User-Id", 64L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(sessionService).cancelSession(64L, 5L);
    }

    @Test
    void getSessionById_ReturnsOk() throws Exception {
        when(sessionService.getSessionById(6L)).thenReturn(SessionResponseDto.builder().id(6L).build());

        mockMvc.perform(get("/sessions/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6L));

        verify(sessionService).getSessionById(6L);
    }

    @Test
    void getSessionsByLearner_ReturnsOk() throws Exception {
        when(sessionService.getSessionsByLearner(65L)).thenReturn(List.of(SessionResponseDto.builder().id(7L).learnerId(65L).build()));

        mockMvc.perform(get("/sessions/learner").header("X-User-Id", 65L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].learnerId").value(65L));

        verify(sessionService).getSessionsByLearner(65L);
    }

    @Test
    void getSessionsByMentor_ReturnsOk() throws Exception {
        when(sessionService.getSessionsByMentor(66L)).thenReturn(List.of(SessionResponseDto.builder().id(8L).mentorId(66L).build()));

        mockMvc.perform(get("/sessions/mentor/66"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mentorId").value(66L));

        verify(sessionService).getSessionsByMentor(66L);
    }

    private record SessionRequest(
            Long mentorId,
            Long skillId,
            LocalDateTime scheduledAt,
            Integer durationMin,
            String notes
    ) {}
}
