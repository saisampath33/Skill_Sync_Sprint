package com.skillsync.user.service.impl;

import com.skillsync.user.dto.UserProfileRequestDto;
import com.skillsync.user.dto.UserProfileResponseDto;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.exception.BadRequestException;
import com.skillsync.user.exception.ResourceNotFoundException;
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

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UserProfile userProfile;
    private UserProfileRequestDto requestDto;

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
    }

    @Test
    void createProfile_Success() {
        when(userProfileRepository.existsByUserId(100L)).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        UserProfileResponseDto response = userProfileService.createProfile(100L, requestDto);

        assertNotNull(response);
        assertEquals("John Doe", response.getFullName());
        assertEquals("Java Developer", response.getBio());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void createProfile_ThrowsBadRequest_WhenExists() {
        when(userProfileRepository.existsByUserId(100L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> 
            userProfileService.createProfile(100L, requestDto)
        );
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void getProfileByUserId_Success() {
        when(userProfileRepository.findByUserId(100L)).thenReturn(Optional.of(userProfile));

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
