package com.skillsync.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class MentorResponseDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String status; // MUST be APPROVED to book
}
