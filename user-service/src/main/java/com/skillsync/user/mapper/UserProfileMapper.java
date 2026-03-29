package com.skillsync.user.mapper;

import com.skillsync.user.dto.UserProfileResponseDto;
import com.skillsync.user.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public UserProfileResponseDto toDto(UserProfile profile) {
        if (profile == null) return null;
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
