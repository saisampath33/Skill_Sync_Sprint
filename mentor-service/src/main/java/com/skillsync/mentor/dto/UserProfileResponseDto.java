package com.skillsync.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserProfileResponseDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String bio;
    private String avatarUrl;
    private String location;
    private String linkedinUrl;
}
