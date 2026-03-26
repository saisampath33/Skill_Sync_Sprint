package com.skillsync.mentor.service.interfaces;

import com.skillsync.mentor.dto.MentorApplicationRequestDto;
import com.skillsync.mentor.dto.MentorResponseDto;
import com.skillsync.mentor.entity.MentorAvailability;

import java.util.List;

public interface MentorService {
    MentorResponseDto applyAsMentor(Long userId, String fullName, MentorApplicationRequestDto request);
    MentorResponseDto approveMentor(Long mentorId);
    MentorResponseDto rejectMentor(Long mentorId);
    MentorResponseDto getMentorById(Long mentorId);
    MentorResponseDto getMentorByUserId(Long userId);
    List<MentorResponseDto> searchMentors(Long skillId, Double minRating);
    List<MentorResponseDto> getAllApprovedMentors();
    MentorAvailability addAvailability(Long mentorId, MentorAvailability availability);
    List<MentorAvailability> getAvailability(Long mentorId);
    void updateRating(Long mentorId, Double newRating);
}
