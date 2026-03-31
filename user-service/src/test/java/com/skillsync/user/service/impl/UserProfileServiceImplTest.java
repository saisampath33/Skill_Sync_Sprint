package com.skillsync.user.service.impl;

import com.skillsync.user.dto.UserProfileRequestDto;
import com.skillsync.user.dto.UserProfileResponseDto;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.exception.ResourceNotFoundException;
import com.skillsync.user.mapper.UserProfileMapper;
import com.skillsync.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UserProfile userProfile;
    private UserProfileRequestDto requestDto;
    private UserProfileResponseDto responseDto;

    @BeforeEach
    void setUp() {
        userProfile = UserProfile.builder()
                .id(1L)
                .userId(100L)
                .fullName("John Doe")
                .bio("Java Developer")
                .build();

        requestDto = new UserProfileRequestDto();
        requestDto.setFullName("John Doe");
        requestDto.setBio("Java Developer");

        responseDto = UserProfileResponseDto.builder()
                .userId(100L)
                .fullName("John Doe")
                .bio("Java Developer")
                .build();
    }

    @Test
    void createProfile_Success() {
        when(userProfileRepository.findByUserId(100L)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);
        when(userProfileMapper.toDto(any(UserProfile.class))).thenReturn(responseDto);

        UserProfileResponseDto response = userProfileService.createProfile(100L, requestDto);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        assertEquals("Java Developer", response.getBio());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void createProfile_UpdatesExistingProfile_WhenExists() {
        when(userProfileRepository.findByUserId(100L)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.save(userProfile)).thenReturn(userProfile);
        when(userProfileMapper.toDto(userProfile)).thenReturn(responseDto);

        UserProfileResponseDto response = userProfileService.createProfile(100L, requestDto);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        verify(userProfileRepository).save(userProfile);
    }

    @Test
    void getProfileByUserId_Success() {
        when(userProfileRepository.findByUserId(100L)).thenReturn(Optional.of(userProfile));
        when(userProfileMapper.toDto(userProfile)).thenReturn(responseDto);

        UserProfileResponseDto response = userProfileService.getProfileByUserId(100L);

        assertNotNull(response);
        assertEquals(100L, response.getUserId());
    }

    @Test
    void getProfileByUserId_ThrowsNotFound() {
        when(userProfileRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            userProfileService.getProfileByUserId(999L)
        );
    }
}
