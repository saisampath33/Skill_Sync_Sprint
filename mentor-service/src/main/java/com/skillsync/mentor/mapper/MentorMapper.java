package com.skillsync.mentor.mapper;

import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.dto.UserProfileResponseDto;
import com.skillsync.mentor.entity.Mentor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MentorMapper {

    public MentorResponseDto toDto(Mentor mentor, List<Long> skillIds, UserProfileResponseDto userProfile) {
        if (mentor == null) return null;

        String fullName = (userProfile != null) ? userProfile.getFullName() : null;
        String bio = (userProfile != null) ? userProfile.getBio() : null;

        return MentorResponseDto.builder()
                .id(mentor.getId())
                .userId(mentor.getUserId())
                .fullName(fullName)
                .bio(bio)
                .status(mentor.getStatus())
                .experience(mentor.getExperience())
                .hourlyRate(mentor.getHourlyRate())
                .rating(mentor.getRating())
                .totalReviews(mentor.getTotalReviews())
                .skillIds(skillIds)
                .approvedAt(mentor.getApprovedAt())
                .build();
    }
}
