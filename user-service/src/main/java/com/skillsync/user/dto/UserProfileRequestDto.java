package com.skillsync.user.dto;

import lombok.Data;

@Data
public class UserProfileRequestDto {
    private String fullName;
    private String bio;
    private String avatarUrl;
    private String location;
    private String linkedinUrl;
}
