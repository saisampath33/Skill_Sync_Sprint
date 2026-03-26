package com.skillsync.user.service.interfaces;

import com.skillsync.user.dto.UserProfileRequestDto;
import com.skillsync.user.dto.UserProfileResponseDto;

import java.util.List;

public interface UserProfileService {
    UserProfileResponseDto createProfile(Long userId, UserProfileRequestDto request);
    UserProfileResponseDto updateProfile(Long userId, UserProfileRequestDto request);
    UserProfileResponseDto getProfileByUserId(Long userId);
    List<UserProfileResponseDto> getAllProfiles();
    void deleteProfile(Long userId);
}
