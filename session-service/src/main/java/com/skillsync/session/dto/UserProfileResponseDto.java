package com.skillsync.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserProfileResponseDto {
    private Long id;
    private Long userId;
    private String fullName;
}
