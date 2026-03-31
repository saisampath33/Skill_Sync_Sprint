package com.skillsync.mentor.service.impl;

import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.dto.UserProfileResponseDto;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.feign.UserFeignClient;
import com.skillsync.mentor.mapper.MentorMapper;
import com.skillsync.mentor.repository.MentorRepository;
import com.skillsync.mentor.repository.MentorSkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorServiceImplTest {

    @Mock
    private MentorRepository mentorRepository;
    
    @Mock
    private MentorSkillRepository mentorSkillRepository;
    
    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private MentorMapper mentorMapper;

    @InjectMocks
    private MentorServiceImpl mentorService;

    @Test
    void getMentorById_Success() {
        Mentor mentor = Mentor.builder()
                .id(1L)
                .userId(100L)
                .experience(5)
                .hourlyRate(BigDecimal.valueOf(25.0))
                .status(Mentor.MentorStatus.APPROVED)
                .build();

        MentorResponseDto responseDto = MentorResponseDto.builder()
                .userId(100L)
                .status(Mentor.MentorStatus.APPROVED)
                .build();

        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(mentorSkillRepository.findByMentorId(1L)).thenReturn(List.of());
        
        UserProfileResponseDto profile = new UserProfileResponseDto();
        profile.setFullName("Mentor Name");
        when(userFeignClient.getUserProfile(100L)).thenReturn(profile);
        when(mentorMapper.toDto(eq(mentor), any(), any())).thenReturn(responseDto);

        MentorResponseDto response = mentorService.getMentorById(1L);

        assertNotNull(response);
        assertEquals(100L, response.getUserId());
        assertEquals(Mentor.MentorStatus.APPROVED, response.getStatus());
    }
}
