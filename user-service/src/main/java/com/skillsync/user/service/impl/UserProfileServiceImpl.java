package com.skillsync.user.service.impl;

import com.skillsync.user.dto.UserProfileRequestDto;
import com.skillsync.user.dto.UserProfileResponseDto;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.exception.BadRequestException;
import com.skillsync.user.exception.ResourceNotFoundException;
import com.skillsync.user.mapper.UserProfileMapper;
import com.skillsync.user.repository.UserProfileRepository;
import com.skillsync.user.service.interfaces.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "userProfile", key = "#userId"),
        @CacheEvict(value = "userProfiles", allEntries = true)
    })
    public UserProfileResponseDto createProfile(Long userId, UserProfileRequestDto request) {
        return userProfileRepository.findByUserId(userId)
                .map(existingProfile -> {
                    if (request.getFullName() != null) existingProfile.setFullName(request.getFullName());
                    if (request.getBio() != null) existingProfile.setBio(request.getBio());
                    if (request.getAvatarUrl() != null) existingProfile.setAvatarUrl(request.getAvatarUrl());
                    if (request.getLocation() != null) existingProfile.setLocation(request.getLocation());
                    if (request.getLinkedinUrl() != null) existingProfile.setLinkedinUrl(request.getLinkedinUrl());
                    log.info("Profile updated via POST (upsert) for userId: {}", userId);
                    return userProfileMapper.toDto(userProfileRepository.save(existingProfile));
                })
                .orElseGet(() -> {
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
                    return userProfileMapper.toDto(saved);
                });
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "userProfile", key = "#userId"),
        @CacheEvict(value = "userProfiles", allEntries = true)
    })
    public UserProfileResponseDto updateProfile(Long userId, UserProfileRequestDto request) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        if (request.getFullName()    != null) profile.setFullName(request.getFullName());
        if (request.getBio()         != null) profile.setBio(request.getBio());
        if (request.getAvatarUrl()   != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getLocation()    != null) profile.setLocation(request.getLocation());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());

        return userProfileMapper.toDto(userProfileRepository.save(profile));
    }

    @Override
    @Cacheable(value = "userProfile", key = "#userId")
    public UserProfileResponseDto getProfileByUserId(Long userId) {
        log.info("[CACHE MISS] Fetching profile for userId {} from DB", userId);
        return userProfileRepository.findByUserId(userId)
                .map(userProfileMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
    }

    @Override
    @Cacheable(value = "userProfiles", key = "'all'")
    public List<UserProfileResponseDto> getAllProfiles() {
        log.info("[CACHE MISS] Fetching all profiles from DB");
        return userProfileRepository.findAll().stream()
                .map(userProfileMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "userProfile", key = "#userId"),
        @CacheEvict(value = "userProfiles", allEntries = true)
    })
    public void deleteProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
        userProfileRepository.delete(profile);
        log.info("Profile deleted for userId: {}", userId);
    }
}
