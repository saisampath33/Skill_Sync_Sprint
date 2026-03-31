package com.skillsync.session.service.impl;

import com.skillsync.session.dto.SessionResponseDto;
import com.skillsync.session.entity.Session;
import com.skillsync.session.mapper.SessionMapper;
import com.skillsync.session.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @Test
    void getSessionById_Success() {
        Session session = Session.builder()
                .id(10L)
                .learnerId(50L)
                .mentorId(100L)
                .status(Session.SessionStatus.REQUESTED)
                .build();

        SessionResponseDto responseDto = SessionResponseDto.builder()
                .id(10L)
                .learnerId(50L)
                .status(Session.SessionStatus.REQUESTED)
                .build();

        when(sessionRepository.findById(10L)).thenReturn(Optional.of(session));
        when(sessionMapper.toDto(eq(session), anyString())).thenReturn(responseDto);

        SessionResponseDto response = sessionService.getSessionById(10L);

        assertNotNull(response);
        assertEquals(50L, response.getLearnerId());
        assertEquals(Session.SessionStatus.REQUESTED, response.getStatus());
    }
}
