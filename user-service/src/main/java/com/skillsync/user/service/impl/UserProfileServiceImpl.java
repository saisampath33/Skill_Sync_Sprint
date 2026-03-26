package com.skillsync.user.service.impl;

import com.skillsync.user.dto.UserProfileRequestDto;
import com.skillsync.user.dto.UserProfileResponseDto;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.exception.BadRequestException;
import com.skillsync.user.exception.ResourceNotFoundException;
import com.skillsync.user.repository.UserProfileRepository;
import com.skillsync.user.service.interfaces.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public UserProfileResponseDto createProfile(Long userId, UserProfileRequestDto request) {
        if (userProfileRepository.existsByUserId(userId)) {
            throw new BadRequestException("Profile already exists for userId: " + userId);
        }

        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .fullName(request.getFullName())
                .bio(request.getBio())
                .avatarUrl(request.getAvatarUrl())
                .location(request.getLocation())
                .linkedinUrl(request.getLinkedinUrl())
                .build();

        UserProfile saved = userProfileRepository.save(profile);
        log.info("Profile created for userId: {}", userId);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateProfile(Long userId, UserProfileRequestDto request) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        if (request.getFullName()   != null) profile.setFullName(request.getFullName());
        if (request.getBio()        != null) profile.setBio(request.getBio());
        if (request.getAvatarUrl()  != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getLocation()   != null) profile.setLocation(request.getLocation());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());

        return mapToDto(userProfileRepository.save(profile));
    }

    @Override
    public UserProfileResponseDto getProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
    }

    @Override
    public List<UserProfileResponseDto> getAllProfiles() {
        return userProfileRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
        userProfileRepository.delete(profile);
        log.info("Profile deleted for userId: {}", userId);
    }

    private UserProfileResponseDto mapToDto(UserProfile profile) {
        return UserProfileResponseDto.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .location(profile.getLocation())
                .linkedinUrl(profile.getLinkedinUrl())
                .build();
    }
}
